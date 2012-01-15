import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def protein(MIPS):
    w = Workflow(name="protein", description="""Protein annotation workflow (Figure 4c in Yu et al)""")
    
    signalp = Job(id="SignalP", namespace="protein", name="SignalP", runtime=(300000/MIPS)*SECONDS)
    w.addJob(signalp)
    
    coils2 = Job(id="COILS2", namespace="protein", name="COILS2", runtime=(600000/MIPS)*SECONDS)
    w.addJob(coils2)
    
    seg = Job(id="SEG", namespace="protein", name="SEG", runtime=(600000/MIPS)*SECONDS)
    w.addJob(seg)
    
    prosite = Job(id="PROSITE", namespace="protein", name="PROSITE", runtime=(900000/MIPS)*SECONDS)
    w.addJob(prosite)
    
    tmhmm = Job(id="TMHMM", namespace="protein", name="TMHMM", runtime=(300000/MIPS)*SECONDS, parents=[signalp])
    w.addJob(tmhmm)
    
    prospero = Job(id="Prospero", namespace="protein", name="Prospero", runtime=(150000/MIPS)*SECONDS, parents=[tmhmm, coils2, seg])
    w.addJob(prospero)
    
    hmmer = Job(id="HMMer", namespace="protein", name="HMMer", runtime=(150000/MIPS)*SECONDS, parents=[seg])
    w.addJob(hmmer)
    
    psiblast = Job(id="PSI-BLAST", namespace="protein", name="PSI-BLAST", runtime=(300000/MIPS)*SECONDS, parents=[prospero])
    w.addJob(psiblast)
    
    blast = Job(id="BLAST", namespace="protein", name="BLAST", runtime=(300000/MIPS)*SECONDS, parents=[prospero])
    w.addJob(blast)
    
    impala = Job(id="IMPALA", namespace="protein", name="IMPALA", runtime=(300000/MIPS)*SECONDS, parents=[prospero])
    w.addJob(impala)
    
    psipred = Job(id="PSI-PRED", namespace="protein", name="PSI-PRED", runtime=(600000/MIPS)*SECONDS, parents=[psiblast])
    w.addJob(psipred)
    
    tdpssm = Job(id="3D-PSSM", namespace="protein", name="3D-PSSM", runtime=(300000/MIPS)*SECONDS, parents=[psipred])
    w.addJob(tdpssm)
    
    summary = Job(id="Summary", namespace="protein", name="Summary", runtime=(600000/MIPS)*SECONDS, parents=[psiblast, blast, impala, hmmer, prosite])
    w.addJob(summary)
    
    genome = Job(id="Genome", namespace="protein", name="Genome Summary", runtime=(150000/MIPS)*SECONDS, parents=[tdpssm, summary])
    w.addJob(genome)
    
    scop = Job(id="SCOP", namespace="protein", name="SCOP", runtime=(300000/MIPS)*SECONDS, parents=[genome])
    w.addJob(scop)
    
    return w

def main(*args):
    class Protein(Main):
        def setoptions(self, parser):
            self.parser.add_option("-M", "--mips", dest="MIPS", metavar="MIPS", type="int", default=1200,
                help="MIPS of PE [default: %default]")
        
        def genworkflow(self, options):
            return protein(options.MIPS)
    
    Protein().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
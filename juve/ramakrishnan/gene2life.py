import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def gene2life():
    w = Workflow(name="gene2life", description="""Gene2Life bioinformatics workflow (Figure 7 in Ramakrishnan and Gannon)""")
    
    wfin = File(name="wf_in.dat", size=0.1*MB)
    
    blast1out1 = File(name="blast1_out1.dat", size=1*MB)
    blast1out2 = File(name="blast1_out2.dat", size=0.1*MB)
    blast1 = Job(id="blast1", namespace="gene2life", name="blast", runtime=180*SECONDS, inputs=[wfin], outputs=[blast1out1, blast1out2])
    w.addJob(blast1)
    
    blast2out1 = File(name="blast2_out1.dat", size=1*MB)
    blast2out2 = File(name="blast2_out2.dat", size=0.1*MB)
    blast2 = Job(id="blast2", namespace="gene2life", name="blast", runtime=180*SECONDS, inputs=[wfin], outputs=[blast2out1, blast2out2])
    w.addJob(blast2)
    
    clustalw1out1 = File(name="clustalw1_out1.dat", size=0.1*MB)
    clustalw1out2 = File(name="clustalw1_out2.dat", size=4*KB)
    clustalw1 = Job(id="clustalw1", namespace="gene2life", name="clustalw", runtime=300*SECONDS, inputs=[blast1out2], outputs=[clustalw1out1, clustalw1out2])
    w.addJob(clustalw1)
    
    clustalw2out1 = File(name="clustalw2_out1.dat", size=0.1*MB)
    clustalw2out2 = File(name="clustalw2_out2.dat", size=4*KB)
    clustalw2 = Job(id="clustalw2", namespace="gene2life", name="clustalw", runtime=300*SECONDS, inputs=[blast2out2], outputs=[clustalw2out1, clustalw2out2])
    w.addJob(clustalw2)
    
    dnaparsout = File(name="dnapars_out.dat", size=4*KB)
    dnapars = Job(id="dnapars", namespace="gene2life", name="dnapars", runtime=30*SECONDS, inputs=[clustalw1out2], outputs=[dnaparsout])
    w.addJob(dnapars)
    
    protparsout = File(name="protpars_out.dat", size=4*KB)
    protpars = Job(id="protpars", namespace="gene2life", name="protpars", runtime=30*SECONDS, inputs=[clustalw2out2], outputs=[protparsout])
    w.addJob(protpars)
    
    drawgram1out = File(name="drawgram1_out.dat", size=35*KB)
    drawgram1 = Job(id="drawgram1", namespace="gene2life", name="drawgram", runtime=30*SECONDS, inputs=[dnaparsout], outputs=[drawgram1out])
    w.addJob(drawgram1)
    
    drawgram2out = File(name="drawgram2_out.dat", size=35*KB)
    drawgram2 = Job(id="drawgram2", namespace="gene2life", name="drawgram", runtime=30*SECONDS, inputs=[protparsout], outputs=[drawgram2out])
    w.addJob(drawgram2)
    
    return w

def main(*args):
    class Gene2Life(Main):
        def genworkflow(self, options):
            return gene2life()
    
    Gene2Life().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

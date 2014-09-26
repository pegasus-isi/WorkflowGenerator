import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def molsci():
    w = Workflow(name="molsci", description="""Molecular Sciences workflow (Figure 10 in Ramakrishnan and Gannon)""")
    
    babelin = File(name="babel_in.dat", size=100*KB)
    babelout = File(name="babel_out.dat", size=120*KB)
    babel = Job(id="babel", namespace="molsci", name="BABEL", runtime=60*SECONDS, inputs=[babelin], outputs=[babelout])
    w.addJob(babel)
    
    lightout = File(name="lightprep_out.dat", size=140*KB)
    lightprep = Job(id="lightprep", namespace="molsci", name="LightPrep", runtime=60*SECONDS, inputs=[babelout], outputs=[lightout])
    w.addJob(lightprep)
    
    gamessout = File(name="gamess_out.dat", size=175*KB)
    gamess = Job(id="gamess", namespace="molsci", name="GAMESS", runtime=5*MINUTES, inputs=[lightout], outputs=[gamessout])
    w.addJob(gamess)
    
    pdbin = File(name="pdb_in.dat", size=2*MB)
    pqrout = File(name="pqr_out.dat", size=2.2*MB)
    pdb = Job(id="pdb2pqr", namespace="molsci", name="PDB2PQR", runtime=5*MINUTES, inputs=[pdbin], outputs=[pqrout])
    w.addJob(pdb)
    
    apbsout = File(name="apbs_out.dat", size=50*MB)
    apbs = Job(id="apbs", namespace="molsci", name="APBS", runtime=10*MINUTES, inputs=[gamessout, pqrout], outputs=[apbsout])
    w.addJob(apbs)
    
    return w

def main(*args):
    class MolSci(Main):
        def genworkflow(self, options):
            return molsci()
    
    MolSci().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

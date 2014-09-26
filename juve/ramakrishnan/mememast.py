import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def mememast():
    w = Workflow(name="mememast", description="""MEME-MAST bioinformatics workflow (Figure 9 in Ramakrishnan and Gannon)""")
    
    memein = File(name="meme_in.dat", size=100*KB)
    
    memeout = File(name="meme_out.dat", size=150*KB)
    meme = Job(id="meme", namespace="mememast", name="MEME", runtime=60*SECONDS, inputs=[memein], outputs=[memeout])
    w.addJob(meme)
    
    mastout = File(name="mast_out.dat", size=200*KB)
    mast = Job(id="mast", namespace="mememast", name="MAST", runtime=60*SECONDS, inputs=[memeout], outputs=[mastout])
    w.addJob(mast)
    
    return w

def main(*args):
    class MEMEMAST(Main):
        def genworkflow(self, options):
            return mememast()
    
    MEMEMAST().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def avianflu(N=1000):
    w = Workflow(name="avianflu", description="""Avian Flu drug design workflow (Figure 11 in Ramakrishnan and Gannon)""")
    
    wfin = File(name="input1.dat", size=150*KB)
    wfin2 = File(name="input2.dat", size=50*KB)
    
    prepareout = File(name="prepare_out.dat", size=1*KB)
    prepare = Job(id="prepare", namespace="avianflu", name="PrepareGPF", runtime=2*MINUTES, inputs=[wfin], outputs=[prepareout])
    w.addJob(prepare)
    
    autogridout = File(name="autogrid_out.dat", size=200*KB)
    autogrid = Job(id="autogrid", namespace="avianflu", name="AutoGrid", runtime=4*MINUTES, inputs=[wfin, prepareout], outputs=[autogridout])
    w.addJob(autogrid)
    
    for i in range(1, N+1):
        autodockin1 = File(name="autodock_1in%d.dat"%i, size=7.5*MB)
        autodockin2 = File(name="autodock_2in%d.dat"%i, size=50*KB)
        autodockout = File(name="autodock_out%d.dat"%i, size=200*KB)
        autodock = Job(id="autodock%d"%i, namespace="avianflu", name="AutoDock", runtime=30*MINUTES, inputs=[wfin, autodockin1, autodockin2], outputs=[autodockout])
        w.addJob(autodock)
        
        autogrid.addOutput(autodockin1)
    
    return w

def main(*args):
    class AvianFlu(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numdock", dest="N", metavar="N", type="int", default=1000, 
                help="Number of AutoDock jobs [default: %default]")
        
        def genworkflow(self, options):
            return avianflu(options.N)
    
    AvianFlu().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

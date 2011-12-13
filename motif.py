import sys
from main import Main
from workflow import *

def motif(N=135):
    w = Workflow(name="motif", description="""MotifNetwork bioinformatics workflow (Figure 8 in Ramakrishnan and Gannon)""")
    
    wfin = File(name="wf_in.dat", size=13*MB)
    
    pre = Job(id="pre", namespace="motif", name="Pre Interproscan", runtime=30*SECONDS, inputs=[wfin])
    w.addJob(pre)
    
    postout1 = File(name="post_out1.dat", size=71*MB)
    postout2 = File(name="post_out2.dat", size=599*MB)
    postout3 = File(name="post_out3.dat", size=599*MB)
    post = Job(id="post", namespace="motif", name="Post Interproscan", runtime=60*SECONDS, outputs=[postout1, postout2, postout3])
    
    for i in range(1, N+1):
        scanin = File(name="scan_in%d.dat"%i, size=100*KB)
        scanout = File(name="scan_out%d.dat"%i, size=500*KB)
        scan = Job(id="scan%d"%i, namespace="motif", name="Interproscan", runtime=5400*SECONDS, inputs=[scanin], outputs=[scanout])
        w.addJob(scan)
        pre.addOutput(scanin)
        post.addInput(scanout)
    
    motifout = File(name="motif_out.dat", size=1432*MB)
    motif = Job(id="motif", namespace="motif", name="Motif", runtime=3600*SECONDS, cores=256, inputs=[postout1], outputs=[motifout])
    w.addJob(motif)
    
    return w

def main(*args):
    class Motif(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numscan", dest="N", metavar="N", type="int", default=135, 
                help="Number of interproscan jobs [default: %default]")
        
        def genworkflow(self, options):
            return motif(options.N)
    
    Motif().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *
import math
import random

# A random workflow

def rand(N, alpha, runtimeDist, sizeDist):
    # Width of workflow
    W = int(math.ceil(N/float(alpha)))
    
    # Number of levels
    L = int(math.ceil(N/float(W)))
    
    # Max in degree of any task
    max_id = int(math.floor(W/float(2)))
    
    w = Workflow(name="rand", description="""Random workflow (Figure 2c in Rahman et al)""")
    
    tasks = []
    for i in range(0,N):
        tout = File("task_%d.dat"%i, size=sizeDist()*GB)
        t = Job(id="task_%d"%i, namespace="rand", name="Task", runtime=runtimeDist()*SECONDS, outputs=[tout])
        w.addJob(t)
        tasks.append(t)
    
    for i in range(1,N):
        t = tasks[i]
        k = random.randint(1, min(max_id, i))
        parents = random.sample(tasks[0:i], k)
        for p in parents:
            for o in p.outputs:
                t.addInput(o)
    
    return w

def main(*args):
    class Rand(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numjobs", dest="N", metavar="N", type="int", default=10,
                help="Number of jobs in workflow [default: %default]")
            self.parser.add_option("-a", "--alpha", dest="alpha", metavar="A", type="float", default=5,
                help="Ratio of number of jobs to width (e.g. W = ceil(N/alpha)) [default: %default]")
            
            self.parser.add_option("", "--rtlow", dest="rtlow", metavar="t", type="float", default=100,
                help="Lower bound on runtime in seconds [default: %default]")
            self.parser.add_option("", "--rthigh", dest="rthigh", metavar="t", type="float", default=500,
                help="Upper bound on runtime in seconds [default: %default]")
            
            self.parser.add_option("", "--slow", dest="slow", metavar="t", type="float", default=1,
                help="Lower bound on file size in GB [default: %default]")
            self.parser.add_option("", "--shigh", dest="shigh", metavar="t", type="float", default=5,
                help="Upper bound on file size in GB [default: %default]")
        
        def genworkflow(self, options):
            return rand(options.N, options.alpha, 
                UniformDistribution(options.rtlow, options.rthigh), 
                UniformDistribution(options.slow, options.shigh))
    
    Rand().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

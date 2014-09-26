import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *
import math
import random

# This is another random workflow, but in this version we specify randomly choose
# pairs of tasks to connect. Ensures that each task gets an edge. Because the
# edges are chosen randomly, the actual number of edges in the workflow may be
# less than the user-specified parameter if there are duplicates.

def randpair(N, E, runtimeDist, sizeDist):
    if E < 10:
        print "E must be >= N"
        exit(1)
    
    w = Workflow(name="randpair", description="""Random workflow""")
    
    tasks = []
    for i in range(0,N):
        tout = File("task_%d_out.dat"%i, size=sizeDist()*GB)
        t = Job(id="task_%d"%i, namespace="randpair", name="Task", runtime=runtimeDist()*SECONDS, outputs=[tout])
        w.addJob(t)
        tasks.append(t)
        
    # Each task must have at least one edge
    for i in range(0,N):
        if i >= N/2:
            parent = random.randint(0,i-1)
            child = i
        else:
            parent = i
            child = random.randint(i+1,N-1)
        
        for o in tasks[parent].outputs:
            tasks[child].addInput(o)
    
    # Add any additional edges
    for j in range(0,E-N):
        i = random.randint(0,N-1)
        if i >= N/2:
            parent = random.randint(0,i-1)
            child = i
        else:
            parent = i
            child = random.randint(i+1,N-1)
        
        for o in tasks[parent].outputs:
            tasks[child].addInput(o)
        
    return w

def main(*args):
    class RandPair(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numtasks", dest="tasks", metavar="n", type="int", default=10,
                help="Number of tasks in workflow [default: %default]")
            self.parser.add_option("-E", "--numedges", dest="edges", metavar="n", type="int", default=10,
                help="Number of edges in workflow (must be >= --numjobs) [default: %default]")
            
            self.parser.add_option("", "--rtlow", dest="rtlow", metavar="t", type="float", default=100,
                help="Lower bound on runtime in seconds [default: %default]")
            self.parser.add_option("", "--rthigh", dest="rthigh", metavar="t", type="float", default=500,
                help="Upper bound on runtime in seconds [default: %default]")
            
            self.parser.add_option("", "--slow", dest="slow", metavar="t", type="float", default=1,
                help="Lower bound on file size in GB [default: %default]")
            self.parser.add_option("", "--shigh", dest="shigh", metavar="t", type="float", default=5,
                help="Upper bound on file size in GB [default: %default]")
        
        def genworkflow(self, options):
            return randpair(options.tasks, options.edges, 
                UniformDistribution(options.rtlow, options.rthigh), 
                UniformDistribution(options.slow, options.shigh))
    
    RandPair().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

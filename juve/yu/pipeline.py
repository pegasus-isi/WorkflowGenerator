import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

def pipeline(MIPS):
    w = Workflow(name="pipeline", description="""Pipeline workflow (Figure 4a in Yu et al)""")
    
    a = Job(id="A", namespace="pipeline", name="A", runtime=(300000/MIPS)*SECONDS)
    w.addJob(a)
    
    b = Job(id="B", namespace="pipeline", name="B", runtime=(600000/MIPS)*SECONDS, parents=[a])
    w.addJob(b)
    
    c = Job(id="C", namespace="pipeline", name="C", runtime=(900000/MIPS)*SECONDS, parents=[b])
    w.addJob(c)
    
    d = Job(id="D", namespace="pipeline", name="D", runtime=(150000/MIPS)*SECONDS, parents=[c])
    w.addJob(d)
    
    return w

def main(*args):
    class Pipeline(Main):
        def setoptions(self, parser):
            self.parser.add_option("-M", "--mips", dest="MIPS", metavar="MIPS", type="int", default=1200,
                help="MIPS of PE [default: %default]")
        
        def genworkflow(self, options):
            return pipeline(options.MIPS)
    
    Pipeline().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

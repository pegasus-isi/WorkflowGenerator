import sys
from main import Main
from workflow import *

def mcstas():
    w = Workflow(name="mcstas", description="""McStas neutron ray tracing workflow (Figure 15 in Ramakrishnan and Gannon)""")
    
    vaspout = File(name="vasp_out.dat", size=0.56*MB)
    vasp = Job(id="vasp", namespace="mcstas", name="VASP", runtime=1200*HOURS, cores=16, outputs=[vaspout])
    w.addJob(vasp)
    
    nmoldynout = File(name="nmoldyn_out.dat", size=0.56*MB)
    nmoldyn = Job(id="nmoldyn", namespace="mcstas", name="nMoldyn", runtime=36*HOURS, inputs=[vaspout], outputs=[nmoldynout])
    w.addJob(nmoldyn)
    
    mcsts = Job(id="mcsts", namespace="mcstas", name="McSts", runtime=3*HOURS, cores=128, inputs=[nmoldynout])
    w.addJob(mcsts)
    
    return w

def main(*args):
    class McStas(Main):
        def genworkflow(self, options):
            return mcstas()
    
    McStas().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

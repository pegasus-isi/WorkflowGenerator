from workflow import *

def main(file):
    w = Workflow(name="mcstats", description="""McStats neutron ray tracing workflow (Figure 15 in Ramakrishnan and Gannon)""")
    
    vaspout = File(name="vasp_out.dat", size=0.56*MB)
    vasp = Job(id="vasp", namespace="mcstats", name="VASP", runtime=1200*HOURS, cores=16, outputs=[vaspout])
    w.addJob(vasp)
    
    nmoldynout = File(name="nmoldyn_out.dat", size=0.56*MB)
    nmoldyn = Job(id="nmoldyn", namespace="mcstats", name="nMoldyn", runtime=36*HOURS, inputs=[vaspout], outputs=[nmoldynout])
    w.addJob(nmoldyn)
    
    mcsts = Job(id="mcsts", namespace="mcstats", name="McSts", runtime=3*HOURS, cores=128, inputs=[nmoldynout])
    w.addJob(mcsts)
    
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout")

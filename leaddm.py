from workflow import *

def main(file):
    """LEAD Data Mining workflow (Figure 3 in Ramakrishnan and Gannon)"""
    w = Workflow()
    
    sdin1 = File(name="sd_in1.dat", size=1*KB)
    sdin2 = File(name="sd_in2.dat", size=2*MB)
    
    sdout1 = File(name="sd_out1.dat", size=4*KB)
    sdout2 = File(name="sd_out2.dat", size=1*KB)
    
    sd = Job(id="sd", namespace="leaddm", name="StormDetection", runtime=35*SECONDS, inputs=[sdin1, sdin2], outputs=[sdout1, sdout2])
    w.addJob(sd)
    
    raout1 = File(name="ra_out1.dat", size=1*KB)
    ra = Job(id="ra", namespace="leaddm", name="RemoveAttributes", runtime=66*SECONDS, parents=[sd], inputs=[sdout2], outputs=[raout1])
    w.addJob(ra)
    
    scout1 = File(name="sc_out1.dat", size=5*KB)
    scout2 = File(name="sc_out2.dat", size=9*KB)
    sc = Job(id="sc", namespace="leaddm", name="SpatialClustering", runtime=129*SECONDS, parents=[ra], inputs=[raout1], outputs=[scout1, scout2])
    w.addJob(sc)
    
    w.writeDAX(file)

if __name__ == '__main__':
    main("/dev/stdout")

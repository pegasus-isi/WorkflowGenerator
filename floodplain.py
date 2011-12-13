import sys
from main import Main
from workflow import *

def floodplain():
    w = Workflow(name="floodplain", description="""Floodplain Mapping workflow (Figure 5 in Ramakrishnan and Gannon)""")
    
    adcircin = File("adcirc_in.dat", size=534*MB)
    adcircout = File("adcirc_out.dat", size=21527*MB)
    adcirc = Job(id="adcirc", namespace="floodplain", name="Adcirc", runtime=11*HOURS, cores=256, inputs=[adcircin], outputs=[adcircout])
    w.addJob(adcirc)
    
    ww3in = File(name="ww3_in.dat", size=810*MB)
    ww3out = File(name="ww3_out.dat", size=2766*MB)
    ww3 = Job(id="ww3", namespace="floodplain", name="WaveWatchIII", runtime=1*HOURS, cores=256, inputs=[ww3in], outputs=[ww3out])
    w.addJob(ww3)
    
    swanin = File(name="swan_in.dat", size=34*MB)
    
    sosout = File(name="sos_out.dat", size=14*MB)
    sos = Job(id="sos", namespace="floodplain", name="SWAN Outer South", runtime=8*HOURS, cores=10, inputs=[swanin, adcircout, ww3out], outputs=[sosout])
    w.addJob(sos)
    
    sonout = File(name="son_out.dat", size=11.42*MB)
    son = Job(id="son", namespace="floodplain", name="SWAN Outer North", runtime=13*HOURS, cores=8, inputs=[swanin, adcircout, ww3out], outputs=[sonout])
    w.addJob(son)
    
    sisout = File(name="sis_out.dat", size=4.4*GB)
    sis = Job(id="sis", namespace="floodplain", name="SWAN Inner South", runtime=3*HOURS, cores=192, inputs=[swanin, sosout, adcircout], outputs=[sisout])
    w.addJob(sis)
    
    sinout = File(name="sin_out.dat", size=3.8*GB)
    sin = Job(id="sin", namespace="floodplain", name="SWAN Inner North", runtime=4*HOURS, cores=160, inputs=[swanin, sonout, adcircout], outputs=[sinout])
    w.addJob(sin)
    
    adcirc2in = File(name="adcirc2_in.dat", size=534*MB)
    adcirc2out = File(name="adcirc2_out.dat", size=6501*MB)
    adcirc2 = Job(id="adcirc2", namespace="floodplain", name="Adcirc", runtime=4.5*HOURS, cores=256, inputs=[adcirc2in, sisout, sinout], outputs=[adcirc2out])
    w.addJob(adcirc2)
    
    return w

def main(*args):
    class Floodplain(Main):
        def genworkflow(self, options):
            return floodplain()
    
    Floodplain().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])

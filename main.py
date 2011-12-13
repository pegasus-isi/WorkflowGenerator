from optparse import OptionParser

class Main:
    def __init__(self):
        self.parser = OptionParser()
        self.parser.add_option("-D", "--dax", dest="daxfile",
                help="Write workflow DAX to FILE", metavar="FILE")
        self.parser.add_option("-d", "--dot", dest="dotfile",
                help="Write workflow DOT to FILE", metavar="FILE")
    
    def setoptions(self, parser):
        pass
    
    def genworkflow(self, options):
        pass
    
    def main(self, *argv):
        self.setoptions(self.parser)
        
        (options, args) = self.parser.parse_args(args=list(argv))
        
        if not options.daxfile and not options.dotfile:
            self.parser.error("Specify --dax or --dot")
        
        wf = self.genworkflow(options)
        
        if options.daxfile:
            wf.writeDAX(options.daxfile)
        
        if options.dotfile:
            wf.writeDOT(options.dotfile)


if __name__ == '__main__':
    import sys
    from cadsr import *
    
    class CADSR(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--num", dest="N", help="Number of jobs", metavar="N")
        
        def genworkflow(self, options):
            return cadsr()
    
    CADSR().main(*sys.argv[1:])
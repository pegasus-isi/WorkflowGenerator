if __name__ == '__main__':
    import leadmm
    import leadadas
    import leaddm
    import scoop
    import floodplain
    import glimmer
    import gene2life
    import motif
    import mememast
    import molsci
    import avianflu
    import cadsr
    import psload
    import psmerge
    import mcstas
    
    # DAX files
    leadmm.main("-D", "leadmm.dax")
    leadadas.main("-D", "leadadas.dax")
    leaddm.main("-D", "leaddm.dax")
    floodplain.main("-D", "floodplain.dax")
    glimmer.main("-D", "glimmer.dax")
    gene2life.main("-D", "gene2life.dax")
    mememast.main("-D", "mememast.dax")
    molsci.main("-D", "molsci.dax")
    cadsr.main("-D", "cadsr.dax")
    mcstas.main("-D", "mcstas.dax")
    motif.main("-D", "motif_small.dax", "-N", "10")
    motif.main("-D", "motif_medium.dax", "-N", "135")
    motif.main("-D", "motif_large.dax", "-N", "500")
    psload.main("-D", "psload_small.dax", "-N", "1", "-n", "5")
    psload.main("-D", "psload_medium.dax", "-N", "100", "-n", "5")
    psload.main("-D", "psload_large.dax", "-N", "1000", "-n", "5")
    psmerge.main("-D", "psmerge_small.dax", "-N", "1", "-L", "50", "-H", "100")
    psmerge.main("-D", "psmerge_medium.dax", "-N", "5", "-L", "100", "-H", "200")
    psmerge.main("-D", "psmerge_large.dax", "-N", "16", "-L", "300", "-H", "600")
    scoop.main("-D", "scoop_small.dax", "-N", "5")
    scoop.main("-D", "scoop_medium.dax", "-N", "50")
    scoop.main("-D", "scoop_large.dax", "-N", "100")
    avianflu.main("-D", "avianflu_small.dax", "-N", "100")
    avianflu.main("-D", "avianflu_medium.dax", "-N", "1000")
    avianflu.main("-D", "avianflu_large.dax", "-N", "2000")
    
    # DOT files
    leadmm.main("-d", "leadmm.dot")
    leadadas.main("-d", "leadadas.dot")
    leaddm.main("-d", "leaddm.dot")
    floodplain.main("-d", "floodplain.dot")
    glimmer.main("-d", "glimmer.dot")
    gene2life.main("-d", "gene2life.dot")
    mememast.main("-d", "mememast.dot")
    molsci.main("-d", "molsci.dot")
    cadsr.main("-d", "cadsr.dot")
    mcstas.main("-d", "mcstas.dot")
    motif.main("-d", "motif.dot", "-N", "3")
    psload.main("-d", "psload.dot", "-N", "2", "-n", "3")
    psmerge.main("-d", "psmerge.dot", "-N", "2", "-L", "3", "-H", "3")
    scoop.main("-d", "scoop.dot", "-N", "3")
    avianflu.main("-d", "avianflu.dot", "-N", "3")
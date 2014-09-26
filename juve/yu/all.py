if __name__ == '__main__':
    import pipeline
    import fmri
    import protein
    
    # DAX files
    pipeline.main("-D", "pipeline.xml")
    fmri.main("-D", "fmri.xml")
    protein.main("-D", "protein.xml")
    
    # DOT files
    pipeline.main("-d", "pipeline.dot")
    fmri.main("-d", "fmri.dot")
    protein.main("-d", "protein.dot")

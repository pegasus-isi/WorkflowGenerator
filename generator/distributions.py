import random

class UniformDistribution:
    def __init__(self, low, high):
        self.low = low
        self.high = high
    
    def __call__(self):
        return random.uniform(self.low, self.high)
    
    def __repr__(self):
        return "UniformDistribution(%s, %s)" % (self.low, self.high)

class NormalDistribution:
    def __init__(self, mu, sigma):
        self.mu = mu
        self.sigma = sigma
    
    def __call__(self):
        return random.normalvariate(self.mu, self.sigma)
    
    def __repr__(self):
        return "NormalDistribution(%s, %s)" % (self.mu, self.sigma)

if __name__ == '__main__':
    x = [0]*10
    f = UniformDistribution(0,10)
    for i in range(0, 100):
        x[int(f())] += 1
    print "Uniform:",x
    
    y = [0]*10
    g = NormalDistribution(5, 1)
    for i in range(0, 100):
        y[int(g())] += 1
    print "Normal:", y

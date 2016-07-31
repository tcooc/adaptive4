from math import sqrt
from random import random

iterations = 1000
population = 30
w = 0.792 # intertia weight
c1 = 1.1944 # acceleration coefficient
c2 = c1 # acceleration coefficient
vmax = 1.0**2

class Particle:
  x, v, pbest = [], [], []

def z(x):
  return (4 - 2.1 * x[0]**2 + x[0]**4/3)*x[0]**2 + x[0]*x[1] + (-4+4*x[1]**2)*x[1]**2

swarm = []
gbest = [random() * 10 - 5, random() * 10 - 5] # global best

for _ in range(population):
  p = Particle()
  p.x = [random() * 10 - 5, random() * 10 - 5] # x and y between -5 and 5
  p.v = [0.0, 0.0]
  p.pbest = p.x
  swarm.append(p)

for t in range(iterations):
  r1 = random() # dimension 1 random num
  r2 = random() # dimension 2 random num
  for i in range(len(swarm)):
    p = swarm[i]
    #Nbest = gbest # global best
    Nbest = max([swarm[i - 1].pbest, p.pbest, swarm[(i + 1) % len(swarm)].pbest], key=lambda x: z(x)) # local best
    p.v = [w * p.v[0] + c1 * r1 * (p.pbest[0] - p.x[0]) + c2 * r2 * (Nbest[0] - p.x[0]),
      w * p.v[1] + c1 * r1 * (p.pbest[1] - p.x[1]) + c2 * r2 * (Nbest[1] - p.x[1])]

    # enforce vmax
    v = p.v[0]**2 + p.v[1]**2
    if v > vmax:
      p.v = [p.v[0] / sqrt(v) * vmax, p.v[1] / sqrt(v) * vmax]

    p.x = [p.x[0] + p.v[0], p.x[1] + p.v[1]]

    # enforce domain
    p.x = [max(min(5, p.x[0]), -5), max(min(5, p.x[1]), -5)]

    if z(p.x) <= z(p.pbest):
      p.pbest = p.x
    # asynchronous update
    if z(p.pbest) <= z(gbest):
      gbest = p.pbest
      print(gbest, z(gbest))

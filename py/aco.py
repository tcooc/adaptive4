from math import sqrt
from random import randint, random

ants_number = 50
iterations = 1000
artificial_pheromone = 1.0
evaporation_rate = 0.1
Q = 1.0
alpha = 1.0

Bays29 = [
  [1, 1150.0, 1760.0],
  [2, 630.0, 1660.0],
  [3, 40.0, 2090.0],
  [4, 750.0, 1100.0],
  [5, 750.0, 2030.0],
  [6, 1030.0, 2070.0],
  [7, 1650.0, 650.0],
  [8, 1490.0, 1630.0],
  [9, 790.0, 2260.0],
  [10, 710.0, 1310.0],
  [11, 840.0, 550.0],
  [12, 1170.0, 2300.0],
  [13, 970.0, 1340.0],
  [14, 510.0, 700.0],
  [15, 750.0, 900.0],
  [16, 1280.0, 1200.0],
  [17, 230.0, 590.0],
  [18, 460.0, 860.0],
  [19, 1040.0, 950.0],
  [20, 590.0, 1390.0],
  [21, 830.0, 1770.0],
  [22, 490.0, 500.0],
  [23, 1840.0, 1240.0],
  [24, 1260.0, 1500.0],
  [25, 1280.0, 790.0],
  [26, 490.0, 2130.0],
  [27, 1460.0, 1420.0],
  [28, 1260.0, 1910.0],
  [29, 360.0, 1980.0]
]

def _distance(n1, n2):
  dx = n1[1] - n2[1]
  dy = n1[2] - n2[2]
  return sqrt(dx * dx + dy * dy)

distances = [[] for _ in range(len(Bays29))]
pheromones = [[] for _ in range(len(Bays29))]
ants = [{} for _ in range(ants_number)]

for i in range(len(Bays29)):
  for j in range(i):
    distances[i].append(_distance(Bays29[i], Bays29[j]))
    pheromones[i].append(artificial_pheromone)

def get_distance(n1, n2):
  if n1 < n2:
    return distances[n2][n1]
  return distances[n1][n2]

def get_pheromones(n1, n2):
  if n1 < n2:
    return pheromones[n2][n1]
  return pheromones[n1][n2]

def set_pheromones(n1, n2, value):
  if n1 < n2:
    pheromones[n2][n1] = value
  else:
    pheromones[n1][n2] = value

def get_cost(tour):
  cost = 0
  for i in range(len(tour)):
    cost += get_distance(tour[i - 1], tour[i])
  return cost

for ant in ants:
  ant['edge'] = randint(0, len(Bays29) - 1)
  ant['tabu'] = []

best_tour = [i for i in range(len(Bays29))]
best_tour_cost = get_cost(best_tour)

for iter in range(iterations):
  for ant in ants:
    while True:
      pvalues = [[i, get_pheromones(ant['edge'], i)**alpha] for i in range(len(Bays29)) if i not in ant['tabu'] and i != ant['edge']]

      if len(pvalues) == 0:
        ant['tabu'].append(ant['edge'])
        cost = get_cost(ant['tabu'])
        if cost < best_tour_cost:
          best_tour = ant['tabu']
          best_tour_cost = cost
          print(iter, cost, best_tour, flush=True)
        ant['tabu'] = []
        break

      total_pheromones = 0.0
      for value in pvalues:
        total_pheromones += value[1]

      rand = random()
      total = 0
      for i in range(len(pvalues)):
        total += pvalues[i][1] / total_pheromones
        if total >= rand:
          break
      ant['tabu'].append(ant['edge'])
      ant['edge'] = pvalues[i][0]

      # online pheromone update rules
      #n1, n2 = ant['tabu'][-1], ant['edge']
      #pvalue = get_pheromones(n1, n2)
      #set_pheromones(n1, n2, pvalue + Q / get_distance(n1, n2))

  # evaporate pheromones
  pheromones = [[(1 - evaporation_rate) * pvalue for pvalue in plist] for plist in pheromones]
  # offline pheromone update rules
  for i in range(len(best_tour)):
    n1, n2 = best_tour[i - 1], best_tour[i]
    pvalue = get_pheromones(n1, n2)
    set_pheromones(n1, n2, pvalue * (1 - evaporation_rate) + evaporation_rate * max(max(p) for p in pheromones if p))

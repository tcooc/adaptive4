from random import randint, random

from numpy import arange, sum
from control import step_response, tf, feedback, series

# Kp (2, 18), Ti (1.05, 9.42), Td (0.26, 2.37)
# encode as integers to prevent rounding error, etc.
Kp_range = (200, 1800)
Ti_range = (105, 942)
Td_range = (26, 237)

parameters = 3
individuals = 50
generations = 150
Pcrossover = 0.6
Pmutation = 0.25

# given functions
def stepinfo(res):
  t, y = res
  overshoot = (max(y) / y[-1] - 1) * 100
  if overshoot < 0:
    return (None, None, None)
  try:
    rise1 = t[next(i for i in range(0, len(y) - 1) if y[i] > y[-1] * 0.1)] - t[0]
    rise2 = t[next(i for i in range(0, len(y) - 1) if y[i] > y[-1] * 0.9)] - t[0]
    settle = t[next(len(y)-i for i in range(2, len(y)-1) if abs(y[-i] / y[-1]) > 1.02)] - t[0]
  except StopIteration:
    return (None, None, None)
  return (rise2 - rise1, settle, overshoot)

F = tf(1.0, [1.0, 6.0, 11.0, 6.0, 0])
t = arange(0, 100, 0.01)

def Q2_perfFCN(Kp, Ti, Td):
  G = Kp * tf([Ti * Td, Ti, 1.0], [Ti, 0])

  sys = feedback(series(G,F),1)
  res = step_response(sys, t)

  ISE = sum((val - 1)**2 for val in res[1])
  return (ISE,) + stepinfo(res)

# fitness function
def fitness(individual):
  ISE, Tr, Ts, Mp = Q2_perfFCN(individual[0] / 100.0, individual[1] / 100.0, individual[2] / 100.0)
  if Tr is None:
    return 0
  return 100.0 / (ISE + 1) * (1.0 / (Tr + 1) + 1.0 / (Ts + 1) + 1.0 / (Mp + 1))

def generate_random_individual():
  return [randint(*Kp_range), randint(*Ti_range), randint(*Td_range)]

def main():
  population = [generate_random_individual() for _ in range(individuals)]

  for generation in range(1, generations + 1):
    fitnesses = [fitness(individual) for individual in population]
    total_fitness = float(sum(fitnesses))

    # parent selection
    parents = []
    for _ in range(0, individuals):
      rand = random()
      total= 0
      for i in range(0, individuals):
        total += fitnesses[i] / total_fitness
        if total >= rand:
          break
      parents.append(population[i])

    # crossover
    children = []
    for i in range(0, individuals / 2):
      if random() <= Pcrossover:
        cross_at = randint(0, parameters)
      else:
        cross_at = parameters
      children.append(parents[2 * i][:cross_at] + parents[2 * i + 1][cross_at:])
      children.append(parents[2 * i + 1][:cross_at] + parents[2 * i][cross_at:])

    # mutation
    for i in range(0, individuals):
      for j in range(0, parameters):
        if random() < Pmutation:
          children[i][j] = generate_random_individual()[j]

    children_fitnesses = [fitness(individual) for individual in children]

    # survivor selection
    # 2 elites
    max1 = max2 = 0
    for i in range(1, individuals):
      if fitnesses[i] > fitnesses[max1]:
        max2 = max1
        max1 = i
      elif max2 == max1:
        max2 = i

    # fitness based selection
    min1 = min2 = 0
    for i in range(1, individuals):
      if children_fitnesses[i] < children_fitnesses[min1]:
        min2 = min1
        min1 = i
      elif min2 == min1:
        min2 = i

    children[min1] = population[max1]
    children[min2] = population[max2]

    population = children

    performance = Q2_perfFCN(children[min1][0] / 100.0, children[min1][1] / 100.0, children[min1][2] / 100.0)
    print '%d, %f, %s, %s' % (generation, fitnesses[max1], children[min1], performance)

main()

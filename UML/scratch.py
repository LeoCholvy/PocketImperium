# from builtins import print, len
#
#
# class Cell:
#     def __init__(self):
#         self.ships = []
#         self.system = None
#     def get_number_ships(self):
#         return len(self.ships)
#     def get_system(self):
#         return self.system.level if self.system != None else 0
#     def __str__(self):
#         r = str(self.get_number_ships())+" ships and "
#         if  self.get_system() == 0:
#             r += "no system"
#         else:
#             r += "a level "+str(self.get_system())+" system"
#         return r
#     def __repr__(self):
#         return self.__str__()
#
# class System:
#     def __init__(self, level):
#         self.level = level
#
# class Area:
#     def __init__(self):
#         self.grid = {
#             "haut" : [
#                 [Cell(), Cell(), Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell(), Cell(), Cell()]
#             ],
#             "bas" : [
#                 [Cell(), Cell(), Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell(), Cell(), Cell()]
#             ],
#             "milieu" : [
#                 [Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell(), Cell()],
#                 [Cell(), Cell(), Cell(), Cell()]
#             ]
#         }
#     def generate(self):
#         self.grid["milieu"][1][2].system = System(3)
#
#
# a = Area()
# a.generate()
# print(a.grid)



from builtins import *

class Cell:
    def __init__(self, id):
        # self.ships = []
        self.id = id
        self.system = None
        self.neighbors = []
    def __str__(self):
        if self.system is None:
            return "[]"
        else:
            return "[" + str(self.system) + "]"
    def __repr__(self):
        return self.__str__()

class System:
    def __init__(self, level):
        self.level = level
    def __str__(self):
        return f"<lvl{str(self.level)}>"
    def __repr__(self):
        return self.__str__()

class Area:
    def __init__(self):
        self.grid = [Cell(i) for i in range(47)]
    def __str__(self):
        return str(self.grid)
    def __repr__(self):
        return self.__str__()

map_neighbors = {
    0 : [14, 15, 19, 24, 21, 26, 23, 28, 32, 33],
    1 : [2, 7],
    2 : [1, 3, 7, 8],
    # la suite
}

a = Area()
a.grid[23].system = System(3)
print(a)
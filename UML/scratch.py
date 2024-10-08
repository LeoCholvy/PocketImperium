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


class Cell:
    def __init__(self, id):
        # self.ships = []
        self.id = id
        self.system = None
        self.neighbors = []
    def __str__(self):
        if self.system is None:
            return f"[{str(self.id)}:]"
        else:
            return f"[{str(self.id)}:" + str(self.system) + "]"
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
        self.sectors = [Sector(i) for i in range(9)]
        map_neighbors = {
            0 : [14, 15, 19, 24, 21, 26, 23, 28, 32, 33],
            1 : [2, 7],
            2 : [1, 3, 7, 8],
            3 : [2, 4, 8, 9],
            4 : [3, 5, 9, 10],
            5 : [4, 6, 10, 11],
            6 : [5, 11],
            7 : [1, 2, 8, 12, 13],
            8 : [2, 3, 7, 9, 13, 14],
            9 : [3, 4, 8, 10, 14, 15],
            10 : [4, 5, 9, 11, 15, 16],
            11 : [5, 6, 10, 16, 17],
            12 : [7, 13, 18],
            13 : [7, 8, 12, 14, 18, 19],
            14 : [8, 9, 13, 15, 19, 0],
            15 : [9, 10, 14, 16, 0, 24],
            16 : [10, 11, 15, 17, 24, 25],
            17 : [11, 16, 25],
            18 : [12, 13, 19, 20, 21],
            19 : [13, 14, 18, 0, 21],
            20 : [18, 21, 22],
            21 : [18, 19, 20, 22, 23, 0],
            22 : [20, 21, 23, 30, 31],
            23 : [21, 22, 31, 32, 0],
            24 : [15, 16, 25, 26, 0],
            25 : [16, 17, 24, 26, 27],
            26 : [24, 25, 27, 28, 0, 29],
            27 : [25, 26, 29],
            28 : [26, 29, 33, 34, 0],
            29 : [26, 27, 28, 34, 35],
            30 : [22, 31, 36],
            31 : [22, 23, 30, 32, 36, 37],
            32 : [23, 31, 33, 37, 38, 0],
            33 : [0, 28, 32, 38, 39],
            34 : [28, 29, 33, 35, 39, 40],
            35 : [29, 34, 40],
            36 : [30, 31, 37, 41, 42],
            37 : [31, 32, 36, 38, 42, 43],
            38 : [32, 33, 37, 39, 43, 44],
            39 : [33, 34, 38, 40, 44, 45],
            40 : [34, 35, 39, 45, 46],
            41 : [36, 42],
            42 : [36, 37, 41, 43],
            43 : [37, 38, 42, 44],
            44 : [38, 39, 43, 45],
            45 : [39, 40, 44, 46],
            46 : [40, 45]
        }
        for i in range(47):
            if map_neighbors.get(i) is None:
                continue
            for j in map_neighbors[i]:
                self.grid[i].neighbors.append(self.grid[j])
    def generate(self):
        pass
    def __str__(self):
        return str(self.grid)
    def __repr__(self):
        return self.__str__()

class Sector:
    def __init__(self, id):
        self.id = id
        self.cells = []


a = Area()
a.grid[0].system = System(3)
a.grid[1].system = System(2)
print(a)
print("0:", a.grid[0].neighbors)
print("1:", a.grid[1].neighbors)
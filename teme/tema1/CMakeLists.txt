cmake_minimum_required(VERSION 3.20)
project(tema1 C)

set(CMAKE_C_STANDARD 11)

include_directories(skel)
include_directories(sol)

add_executable(tema1
        skel/genetic_algorithm.c
        skel/genetic_algorithm.h
        skel/individual.h
        skel/sack_object.h
        skel/tema1.c
        sol/genetic_algorithm.c
        sol/genetic_algorithm.h
        sol/individual.h
        sol/sack_object.h
        sol/tema1.c)

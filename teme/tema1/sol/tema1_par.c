#include <stdlib.h>
#include "genetic_algorithm.h"

int main(int argc, char *argv[])
{
    // array with all the objects that can be placed in the sack
    sack_object *objects = NULL;

    // number of objects
    int object_count = 0;

    // maximum weight that can be carried in the sack
    int sack_capacity = 0;

    // number of generations
    int generations_count = 0;

    // number of threads
    int noThreads = 0;

    if (!read_input(&objects, &object_count, &sack_capacity, &generations_count, &noThreads, argc, argv))
    {
        return 0;
    }

    run_genetic_algorithm_par(objects, object_count, generations_count, sack_capacity, noThreads);

    if (objects != NULL)
        free(objects);
    objects = NULL;
    
    return 0;
}

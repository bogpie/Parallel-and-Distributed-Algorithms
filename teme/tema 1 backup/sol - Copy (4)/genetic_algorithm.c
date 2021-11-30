#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "genetic_algorithm.h"
#include <pthread.h>

#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define MAX(x, y) (((x) > (y)) ? (x) : (y))

pthread_barrier_t barrier;

int read_input(sack_object **objects, int *object_count, int *sack_capacity, int *generations_count, int *noThreads,
               int argc, char *argv[])
{
    FILE *fp;

    if (argc < 4)
    {
        fprintf(stderr, "Usage:\n\t./tema1_par in_file generations_count noThreads\n");
        return 0;
    }

    fp = fopen(argv[1], "r");
    if (fp == NULL)
    {
        return 0;
    }

    if (fscanf(fp, "%d %d", object_count, sack_capacity) < 2)
    {
        fclose(fp);
        return 0;
    }

    if (*object_count % 10)
    {
        fclose(fp);
        return 0;
    }

    sack_object *tmp_objects = (sack_object *)calloc(*object_count, sizeof(sack_object));

    for (int i = 0; i < *object_count; ++i)
    {
        if (fscanf(fp, "%d %d", &tmp_objects[i].profit, &tmp_objects[i].weight) < 2)
        {
            free(objects);
            fclose(fp);
            return 0;
        }
    }

    fclose(fp);

    *generations_count = (int)strtol(argv[2], NULL, 10);

    if (*generations_count == 0)
    {
        free(tmp_objects);

        return 0;
    }

    *objects = tmp_objects;

    *noThreads = (int)strtol(argv[3], NULL, 10);

    if (*noThreads == 0)
    {
        return 0;
    }

    return 1;
}

void print_objects(const sack_object *objects, int object_count)
{
    for (int i = 0; i < object_count; ++i)
    {
        printf("%d %d\n", objects[i].weight, objects[i].profit);
    }
}

void print_generation(const individual *generation, int limit)
{
    for (int i = 0; i < limit; ++i)
    {
        for (int j = 0; j < generation[i].chromosome_length; ++j)
        {
            printf("%d ", generation[i].chromosomes[j]);
        }

        printf("\n%d - %d\n", i, generation[i].fitness);
    }
}

void print_best_fitness(const individual *generation)
{
    printf("%d\n", generation[0].fitness);
}

void compute_fitness_function(const sack_object *objects, individual *generation, int object_count, int sack_capacity, struct my_arg *data)
{
    int weight;
    int profit;

    int start = data->id * (double)object_count / data->noThreads;
    int end = MIN((data->id + 1) * (double)object_count / data->noThreads, data->object_count);

    for (int i = start; i < end; ++i)
    {
        weight = 0;
        profit = 0;

        for (int j = 0; j < generation[i].chromosome_length; ++j)
        {
            if (generation[i].chromosomes[j])
            {
                weight += objects[j].weight;
                profit += objects[j].profit;
            }
        }

        generation[i].fitness = (weight <= sack_capacity) ? profit : 0;
    }
}

int cmpfunc(const void *a, const void *b)
{
    int i;
    individual *first = (individual *)a;
    individual *second = (individual *)b;

    int res = second->fitness - first->fitness; // decreasing by fitness
    if (res == 0)
    {
        int first_count = 0, second_count = 0;

        for (i = 0; i < first->chromosome_length && i < second->chromosome_length; ++i)
        {
            first_count += first->chromosomes[i];
            second_count += second->chromosomes[i];
        } // devine o variabila din individ ones

        res = first_count - second_count; // increasing by number of objects in the sack
        if (res == 0)
        {
            return second->index - first->index;
        }
    }

    return res;
}

void mutate_bit_string_1(const individual *ind, int generation_index)
{
    int i, mutation_size;
    int step = 1 + generation_index % (ind->chromosome_length - 2);

    if (ind->index % 2 == 0)
    {
        // for even-indexed individuals, mutate the first 40% chromosomes by a given step
        mutation_size = ind->chromosome_length * 4 / 10;
        for (i = 0; i < mutation_size; i += step)
        {
            ind->chromosomes[i] = 1 - ind->chromosomes[i];
        }
    }
    else
    {
        // for odd-indexed individuals, mutate the last 80% chromosomes by a given step
        mutation_size = ind->chromosome_length * 8 / 10;
        for (i = ind->chromosome_length - mutation_size; i < ind->chromosome_length; i += step)
        {
            ind->chromosomes[i] = 1 - ind->chromosomes[i];
        }
    }
}

void mutate_bit_string_2(const individual *ind, int generation_index)
{
    int step = 1 + generation_index % (ind->chromosome_length - 2);

    // mutate all chromosomes by a given step
    for (int i = 0; i < ind->chromosome_length; i += step)
    {
        ind->chromosomes[i] = 1 - ind->chromosomes[i];
    }
}

void crossover(individual *parent1, individual *child1, int generation_index)
{
    individual *parent2 = parent1 + 1;
    individual *child2 = child1 + 1;
    int count = 1 + generation_index % parent1->chromosome_length;

    memcpy(child1->chromosomes, parent1->chromosomes, count * sizeof(int));
    memcpy(child1->chromosomes + count, parent2->chromosomes + count,
           (parent1->chromosome_length - count) * sizeof(int));

    memcpy(child2->chromosomes, parent2->chromosomes, count * sizeof(int));
    memcpy(child2->chromosomes + count, parent1->chromosomes + count,
           (parent1->chromosome_length - count) * sizeof(int));
}

void copy_individual(const individual *from, const individual *to)
{
    memcpy(to->chromosomes, from->chromosomes, from->chromosome_length * sizeof(int));
}

void free_generation(individual *generation)
{
    int i;

    for (i = 0; i < generation->chromosome_length; ++i)
    {
        free(generation[i].chromosomes);
        generation[i].chromosomes = NULL;
        generation[i].fitness = 0;
    }
}

int findPowerOfTwo(int n)
{
    int left = 0;
    int right = 14;

    while (right > left)
    {
        int mid = (left + right) / 2;
        if (1 << mid < n)
        {
            left = mid + 1;
        }
        else
        {
            right = mid;
        }
    }

    return 1 << left;
}

// qsort(data->current_generation, object_count, sizeof(individual), cmpfunc);
void merge(individual *source, int start, int mid, int end, individual *destination)
{
    int iA = start;
    int iB = mid;
    int i;

    for (i = start; i < end; i++)
    {
        if (end == iB || (iA < mid && cmpfunc(&destination[iA], &destination[iB])))
        {
            destination[i] = source[iA];
            iA++;
        }
        else
        {
            destination[i] = source[iB];
            iB++;
        }
    }
}

void mergeSort(struct my_arg *data)
{
    int i, width;
    // Sorting individuals in a generation
    for (width = 1; width < data->object_count; width = 2 * width)
    {
        int merges = data->object_count / (2 * width);
        if (data->object_count % (2 * width))
        {
            ++merges;
        }
        int start = data->id * merges / data->noThreads * 2 * width;
        int end = MIN((data->id + 1) * merges / data->noThreads * 2 * width, data->noThreads);

        i = start;
        while (i < end)
        {
            // if index merge == merge && merges % 2
            // continue // break;
            int portionStart = i;
            int portionMid = MIN(i + width, end);
            int portionEnd = MIN(i + 2 * width, end);

            merge(data->current_generation, portionStart, portionMid, portionEnd, data->tmp);
            i = i + 2 * width;
        }

        pthread_barrier_wait(&barrier);

        //aux = v;
        //v = vNew;
        //vNew = aux;
        //pthread_barrier_wait(&barrier);
    }
}

void *thread_function(void *arg)
{
    struct my_arg *data = (struct my_arg *)arg;

    int generations_count = data->generations_count;
    int object_count = data->object_count;
    const sack_object *objects = data->objects;
    int sack_capacity = data->sack_capacity;

    int count, cursor;

    // set initial generation (composed of object_count individuals with a single item in the sack)

    int start = data->id * (double)data->object_count / data->noThreads;
    int end = MIN((data->object_count + 1) * (double)data->object_count / data->noThreads, data->object_count);

    for (int i = start; i < end; ++i)
    {
        data->current_generation[i].fitness = 0;
        data->current_generation[i].chromosomes = (int *)calloc(object_count, sizeof(int));
        data->current_generation[i].chromosomes[i] = 1;
        data->current_generation[i].index = i;
        data->current_generation[i].chromosome_length = object_count;

        data->next_generation[i].fitness = 0;
        data->next_generation[i].chromosomes = (int *)calloc(object_count, sizeof(int));
        data->next_generation[i].index = i;
        data->next_generation[i].chromosome_length = object_count;
    }

    pthread_barrier_wait(&barrier);

    // iterate for each generation
    for (int k = 0; k < generations_count; ++k)
    {

        pthread_barrier_wait(&barrier);
        compute_fitness_function(objects, data->current_generation, object_count, sack_capacity, data);
        pthread_barrier_wait(&barrier);

        // compute fitness and sort by it
        if (data->id == 0)
        {
            cursor = 0;

            // mergeSort(data);
            qsort(data->current_generation, object_count, sizeof(individual), cmpfunc);
        }

        pthread_barrier_wait(&barrier);

        // keep first 30% children (elite children selection)
        count = object_count * 3 / 10;
        start = data->id * (double)count / data->noThreads;
        end = MIN((count + 1) * (double)count / data->noThreads, count);

        for (int i = start; i < end; ++i)
        {
            copy_individual(data->current_generation + i, data->next_generation + i);
        }

        pthread_barrier_wait(&barrier);

        if (data->id == 0)
        {
            cursor = count;

            // mutate first 20% children with the first version of bit string mutation
            count = object_count * 2 / 10;

            for (int i = 0; i < count; ++i)
            {
                copy_individual(data->current_generation + i, data->next_generation + cursor + i);
                mutate_bit_string_1(data->next_generation + cursor + i, k);
            }
            cursor += count;

            // mutate next 20% children with the second version of bit string mutation
            count = object_count * 2 / 10;
            for (int i = 0; i < count; ++i)
            {
                copy_individual(data->current_generation + i + count, data->next_generation + cursor + i);
                mutate_bit_string_2(data->next_generation + cursor + i, k);
            }
            cursor += count;

            // crossover first 30% parents with one-point crossover
            // (if there is an odd number of parents, the last one is kept as such)
            count = object_count * 3 / 10;

            if (count % 2 == 1)
            {
                copy_individual(data->current_generation + object_count - 1, data->next_generation + cursor + count - 1);
                count--;
            }

            for (int i = 0; i < count; i += 2)
            {
                crossover(data->current_generation + i, data->next_generation + cursor + i, k);
            }
        }
        
        pthread_barrier_wait(&barrier);

        // switch to new generation
        data->tmp = data->current_generation;
        data->current_generation = data->next_generation;
        data->next_generation = data->tmp;
        pthread_barrier_wait(&barrier);

        if (data->id == 0)
        {

            for (int i = 0; i < object_count; ++i)
            {
                data->current_generation[i].index = i;
            }

            if (k % 5 == 0)
            {
                print_best_fitness(data->current_generation);
            }
        }

        pthread_barrier_wait(&barrier);
    }

    pthread_barrier_wait(&barrier);
    compute_fitness_function(objects, data->current_generation, object_count, sack_capacity, data);
    pthread_barrier_wait(&barrier);

    if (data->id == 0)
    {
        print_best_fitness(data->current_generation);
    }

    pthread_exit(NULL);
}

void run_genetic_algorithm_par(const sack_object *objects, int object_count, int generations_count, int sack_capacity,
                               int noThreads)
{

    individual *current_generation = (individual *)calloc(object_count, sizeof(individual));
    individual *next_generation = (individual *)calloc(object_count, sizeof(individual));
    individual *tmp = (individual *)calloc(object_count, sizeof(individual));

    struct my_arg *arguments;
    pthread_t *threads;

    threads = (pthread_t *)malloc(noThreads * sizeof(pthread_t));
    arguments = (struct my_arg *)malloc(noThreads * sizeof(struct my_arg));

    pthread_barrier_init(&barrier, NULL, noThreads);

    for (int idThread = 0; idThread < noThreads; ++idThread)
    {
        arguments[idThread].current_generation = current_generation;
        arguments[idThread].generations_count = generations_count;
        arguments[idThread].id = idThread;
        arguments[idThread].next_generation = next_generation;
        arguments[idThread].noThreads = noThreads;
        arguments[idThread].object_count = object_count;
        arguments[idThread].objects = objects;
        arguments[idThread].sack_capacity = sack_capacity;
        arguments[idThread].tmp = tmp;

        int result = pthread_create(&threads[idThread], NULL, thread_function, &arguments[idThread]);
        if (result)
        {
            printf("Eroare la crearea thread-ului %d\n", idThread);
            exit(-1);
        }
    }

    for (int idThread = 0; idThread < noThreads; ++idThread)
    {
        int result = pthread_join(threads[idThread], NULL);
        if (result)
        {
            printf("Eroare la crearea thread-ului %d\n", idThread);
            exit(-1);
        }
    }

    pthread_barrier_destroy(&barrier);

    // free resources for old generation
    free_generation(current_generation);
    free_generation(next_generation);

    // free resources
    free(current_generation);
    free(next_generation);
}
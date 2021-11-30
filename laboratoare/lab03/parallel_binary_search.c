#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>

#define INSIDE -1 // daca numarul cautat este in interiorul intervalului
#define OUTSIDE -2 // daca numarul cautat este in afara intervalului

#define MIN(x, y) (((x) < (y)) ? (x) : (y))
#define MAX(x, y) (((x) > (y)) ? (x) : (y))

pthread_barrier_t barrier;

struct my_arg {
	int id;
	int N;
	int P;
	int number;
	int *left;
	int *right;
	int *keep_running;
	int *v;
	int *found;

	int *position;
};

/*
void binary_search() {
	while (keep_running) {
		int mid = left + (right - left) / 2;
		if (left > right) {
			printf("Number not found\n");
			break;
		}

		if (v[mid] == number) {
			keep_running = 0;
			printf("Number found at position %d\n", mid);
		} else if (v[mid] > number) {
			left = mid + 1;
		} else {
			right = mid - 1;
		}
	}
}
*/

void *f(void *arg)
{
	struct my_arg* data = (struct my_arg*) arg;

	while (*data->keep_running) {

		if(*data->keep_running == 1 && data->id == 0)
		{
			for(int i = 0; i < data->P; i++)
			{
				data->found[i] = OUTSIDE;
			}
		}
		
		pthread_barrier_wait(&barrier);

		int size = *data->right - *data->left;

		int start = MIN(size + *data->left, data->id * ceil((double)size  / data->P) + *data->left);
    	int end = MIN(size, (data->id + 1) * ceil((double)size / data->P)) + *data->left;


		if (data->number >= data->v[start] && data->number <= data->v[end - 1]) {
			*data->left = start;
			*data->right = end - 1;
			data->found[data->id] = INSIDE;

			if (data->number == data->v[start]) {
			*data->keep_running = 0;
			*data->position = start;
			} 

			if(data->number == data->v[end - 1]){
				*data->keep_running = 0;
				*data->position = end - 1;
			} 
		}

		pthread_barrier_wait(&barrier);
		
		if(data->id == 0)
		{
			int found = OUTSIDE;
			for(int i = 0; i < data->P; i++)
			{
				if(*data->found == INSIDE){
					found = INSIDE;
					break;
				}
			}
			if (found == OUTSIDE){
				*data ->keep_running = 0;
			}
		}
		pthread_barrier_wait(&barrier);

	}

	pthread_exit(NULL);
}

void display_vector(int *v, int size) {
	int i;

	for (i = 0; i < size; i++) {
		printf("%d ", v[i]);
	}

	printf("\n");
}


int main(int argc, char *argv[])
{
	int r, N, P, number, keep_running, left, right;
	int *v;
	int *found;
	void *status;
	pthread_t *threads;
	struct my_arg *arguments;

	if (argc < 4) {
		printf("Usage:\n\t./ex N P number\n");
		return 1;
	}

	N = atoi(argv[1]);
	P = atoi(argv[2]);
	number = atoi(argv[3]);

	pthread_barrier_init(&barrier, NULL, P);

	keep_running = 1;
	int position = -1;
	left = 0;
	right = N;

	v = (int*) malloc(N * sizeof(int));
	threads = (pthread_t*) malloc(P * sizeof(pthread_t));
	arguments = (struct my_arg*) malloc(P * sizeof(struct my_arg));
	found = (int*) malloc(P * sizeof(int));

	for (int i = 0; i < N; i++) {
		v[i] = i * 2;
	}

	for (int i = 0; i < P; i++) {
		found[i] = OUTSIDE;
	}

	display_vector(v, N);

	for (int i = 0; i < P; i++) {
		arguments[i].id = i;
		arguments[i].N = N;
		arguments[i].P = P;
		arguments[i].number = number;
		arguments[i].left = &left;
		arguments[i].right = &right;
		arguments[i].keep_running = &keep_running;
		arguments[i].v = v;
		arguments[i].found = found;
		arguments[i].position = &position;

		r = pthread_create(&threads[i], NULL, f, &arguments[i]);

		if (r) {
			printf("Eroare la crearea thread-ului %d\n", i);
			exit(-1);
		}
	}

	for (int i = 0; i < P; i++) {
		r = pthread_join(threads[i], &status);

		if (r) {
			printf("Eroare la asteptarea thread-ului %d\n", i);
			exit(-1);
		}
	}

	struct my_arg* data = &arguments[0];
	*found = OUTSIDE;
	for (int i = 0; i < P; i++)
	{
		// printf ("not found FOUND AT %d\n", *data->position);
		if (data->found[i] == INSIDE && *data->position != -1)
		{
			*found = INSIDE;
			printf ("FOUND AT %d\n", *data->position);
			break;
		}
	}
	if(*found == OUTSIDE)
	{
		printf ("DOESN'T EXIST\n");
	}

	pthread_barrier_destroy(&barrier);

	free(v);
	free(threads);
	free(arguments);

	return 0;
}
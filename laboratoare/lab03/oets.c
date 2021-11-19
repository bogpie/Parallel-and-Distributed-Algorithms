#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(x, y) (((x) < (y)) ? (x) : (y))

int N;
int P;
int *v;
int *vQSort;
int sorted = 0;

pthread_barrier_t barrier;

void compare_vectors(int *a, int *b) {
	int i;

	for (i = 0; i < N; i++) {
		if (a[i] != b[i]) {
			printf("Sortare incorecta\n");
			return;
		}
	}

	printf("Sortare corecta\n");
}

void display_vector(int *v) {
	int i;
	int display_width = 2 + log10(N);

	for (i = 0; i < N; i++) {
		printf("%*i", display_width, v[i]);
	}

	printf("\n");
}

int cmp(const void *a, const void *b) {
	int A = *(int*)a;
	int B = *(int*)b;
	return A - B;
}

void get_args(int argc, char **argv)
{
	if(argc < 3) {
		printf("Numar insuficient de parametri: ./oets N P\n");
		exit(1);
	}

	N = atoi(argv[1]);
	P = atoi(argv[2]);
}

void init()
{
	int i;
	v = (int*)malloc(sizeof(int) * N);
	vQSort = (int*)malloc(sizeof(int) * N);

	if (v == NULL || vQSort == NULL) {
		printf("Eroare la malloc!");
		exit(1);
	}

	srand(42);

	for (i = 0; i < N; i++)
		v[i] = rand() % N;
	
}

void print()
{
	printf("v:\n");
	display_vector(v);
	printf("vQSort:\n");
	display_vector(vQSort);
	compare_vectors(v, vQSort);
}

void swap(int *a, int *b) {
	int aux;
	aux = *a;
	*a = *b;
	*b = aux;
}

void* bubbleSort(void* arg)
{
    int id          = *(int*)arg;
    int start = id * ceil((double)N / P);
    int end = MIN(N, (id + 1) * ceil((double)N / P));
	
	int startEven, endEven, startOdd, endOdd;

	if (start % 2 == 0) {
		startEven = start;
		startOdd = start + 1;
	} else {
		startEven = start + 1;
		startOdd = start;
	}

	if (end % 2 == 0) {
		endEven = end;
		endOdd = end + 1;
	} else {
		endEven = end + 1;
		endOdd = end;
	}

	endEven = MIN(N - 1, endEven);
	endOdd = MIN(N - 1, endOdd);


    while(!sorted) {
        pthread_barrier_wait(&barrier);
        sorted = 1;

        for (int i = startEven; i < end; i += 2) {
			if(v[i] > v[i + 1]) {
				swap(&v[i], &v[i + 1]);
				sorted = 0;
			}
		}

        pthread_barrier_wait(&barrier);

        for (int i = startOdd; i < end; i += 2) {
			if(v[i] > v[i + 1]) {
				swap(&v[i], &v[i + 1]);
				sorted = 0;
			}
		}

        pthread_barrier_wait(&barrier);
    }
	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	get_args(argc, argv);
	init();

	int i;
	pthread_t tid[P];
	int thread_id[P];

	display_vector(v);
	// se sorteaza vectorul etalon
	for (i = 0; i < N; i++)
		vQSort[i] = v[i];
	qsort(vQSort, N, sizeof(int), cmp);

	pthread_barrier_init(&barrier, NULL, P);

	// se creeaza thread-urile
	for (i = 0; i < P; i++) {
		thread_id[i] = i;
		pthread_create(&tid[i], NULL, bubbleSort, &thread_id[i]);
	}

	// se asteapta thread-urile
	for (i = 0; i < P; i++) {
		pthread_join(tid[i], NULL);
	}

	// se afiseaza vectorul etalon
	// se afiseaza vectorul curent
	// se compara cele doua
	print();

	pthread_barrier_destroy(&barrier);

	free(v);
	free(vQSort);

	return 0;
}

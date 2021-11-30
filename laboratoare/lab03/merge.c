#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(x, y) (((x) < (y)) ? (x) : (y))

int N;
int P;
int *v;
int *vQSort;
int *vNew;

pthread_barrier_t barrier;

int cmpfunc(int a, int b)
{
	return a < b;
}

void merge(int *source, int start, int mid, int end, int *destination)
{
	int iA = start;
	int iB = mid;
	int i;

	printf("%d, %d, %d\n", start, mid, end);

	for (i = start; i < end; i++)
	{
		if (end == iB || (iA < mid && cmpfunc(source[iA], source[iB])))
		{ // >
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

void compare_vectors(int *a, int *b)
{
	int i;

	for (i = 0; i < N; i++)
	{
		if (a[i] != b[i])
		{
			printf("Sortare incorecta\n");
			return;
		}
	}

	printf("Sortare corecta\n");
}

void display_vector(int *v)
{
	int i;
	int display_width = 2 + log10(N);

	for (i = 0; i < N; i++)
	{
		printf("%*i", display_width, v[i]);
	}

	printf("\n");
}

int cmp(const void *a, const void *b)
{
	int A = *(int *)a;
	int B = *(int *)b;
	return A - B;
}

int is_power_of_two(int n)
{
	if (n == 0)
	{
		return 0;
	}

	return (ceil(log2(n)) == floor(log2(n)));
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

void get_args(int argc, char **argv)
{
	if (argc < 3)
	{
		printf("Numar insuficient de parametri: ./merge N P (N trebuie sa fie putere a lui 2)\n");
		exit(1);
	}

	N = atoi(argv[1]);
	if (!is_power_of_two(N))
	{
		// printf("Actual N is %d\n", N);
		// N = findPowerOfTwo(N);
		// printf("Switching to %d\n", N);

		printf("N trebuie sa fie putere a lui 2\n");
		//exit(1);
	}

	P = atoi(argv[2]);
}

void init()
{
	int i;
	v = (int *)malloc(sizeof(int) * N);
	vQSort = (int *)malloc(sizeof(int) * N);
	vNew = (int *)malloc(sizeof(int) * N);

	if (v == NULL || vQSort == NULL || vNew == NULL)
	{
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

void *thread_function(void *arg)
{
	int thread_id = *(int *)arg;
	// merge sort clasic - trebuie paralelizat
	int i, width, *aux; // width of subarray

	// pentru fiecare etapa
	for (width = 1; width < N; width = 2 * width)
	{

		int merges = N / (2 * width);
		if (N % (2 * width))
		{
			++merges;
		}

		int start = thread_id * merges / P * 2 * width;
		int end = MIN((thread_id + 1) * merges / P * 2 * width, N);
		
		pthread_barrier_wait(&barrier);
		printf("merges: %d, id: %d, start: %d, end:%d, width: %d \n", merges, thread_id, start, end, width);
		pthread_barrier_wait(&barrier);

		//start si end reprezinta cum se impart intre threaduri

		i = start;
		while (i < end)
		{
			// if index merge == merge && merges % 2
			// continue // break;
			int portionStart = i;
			int portionMid = MIN(i + width, end);
			int portionEnd = MIN(i + 2 * width, end);

			if (portionEnd > end)
			{
				// portionEnd = end;

				printf("id: %d, start: %d, end:%d, width: %d, portionMid: %d \n", thread_id, portionStart, portionEnd, width, portionMid);

				// int offset = width/2 > 0 ? (N%(width/2)) : 0;
				// portionMid = portionEnd - offset;

			}

			merge(v, portionStart, portionMid, portionEnd, vNew);
			i = i + 2 * width;
		}

		printf("\n");

		// BARRIER : si punem threadul 0 sa faca o singura operatie
		pthread_barrier_wait(&barrier);
		if (thread_id == 0) // sunt variabile globale
		{
			aux = v;
			v = vNew;
			vNew = aux;

		}

		// BARRIER : blochez toate threadurile pana cand threadul 0 si-a facut treaba
		pthread_barrier_wait(&barrier);
		if (thread_id == 0)
			print();
		pthread_barrier_wait(&barrier);
	}

	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	get_args(argc, argv);
	init();

	int i;
	int thread_id[P];
	pthread_t tid[P];

	pthread_barrier_init(&barrier, NULL, P);

	// se sorteaza vectorul etalon
	for (i = 0; i < N; i++)
		vQSort[i] = v[i];
	qsort(vQSort, N, sizeof(int), cmp);
	print();

	// se creeaza thread-urile
	for (i = 0; i < P; i++)
	{
		thread_id[i] = i;
		pthread_create(&tid[i], NULL, thread_function, &thread_id[i]);
	}

	// se asteapta thread-urile
	for (i = 0; i < P; i++)
	{
		pthread_join(tid[i], NULL);
	}

	// // merge sort clasic - trebuie paralelizat
	// int width, *aux; // width of subarray
	// for (width = 1; width < N; width = 2 * width) {
	// 	for (i = 0; i < N; i = i + 2 * width) {
	// 		merge(v, i, i + width, i + 2 * width, vNew);
	// 	}

	// 	aux = v;
	// 	v = vNew;
	// 	vNew = aux;
	// }

	print();

	pthread_barrier_destroy(&barrier);

	free(v);
	free(vQSort);
	free(vNew);

	return 0;
}

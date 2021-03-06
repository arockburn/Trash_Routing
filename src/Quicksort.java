/**
 * This is a quicksort of an array of float
 * @author Lars Vogel
 * Updated and improved by Alexander Rorick
 *
 */
public class Quicksort  {
  private static float[] numbers;
  private static int number;
  
  public static float[] sort(float[] values) {
    numbers = new float[values.length];
    for (int i = 0; i < values.length; i++){
    	numbers[i] = values[i];
    }
    number = values.length;
    quicksort(0, number - 1);
    return numbers;
  }

  private static void quicksort(int low, int high) {
    int i = low, j = high;
    // Get the pivot element from the middle of the list
    float pivot = numbers[low + (high-low)/2];

    // Divide into two lists
    while (i <= j) {
      // If the current value from the left list is smaller then the pivot
      // element then get the next element from the left list
      while (numbers[i] < pivot) {
        i++;
      }
      // If the current value from the right list is larger then the pivot
      // element then get the next element from the right list
      while (numbers[j] > pivot) {
        j--;
      }

      // If we have found a values in the left list which is larger then
      // the pivot element and if we have found a value in the right list
      // which is smaller then the pivot element then we exchange the
      // values.
      // As we are done we can increase i and j
      if (i <= j) {
        exchange(i, j);
        i++;
        j--;
      }
    }
    // Recursion
    if (low < j)
      quicksort(low, j);
    if (i < high)
      quicksort(i, high);
  }

  private static void exchange(int i, int j) {
    float temp = numbers[i];
    numbers[i] = numbers[j];
    numbers[j] = temp;
  }
} 
import java.util.*;

public class test {


    public static void main(String[] args) {


        getStus(2, 1, 2, 3, 2, 3, 1, 1, 1);

    }


    public static void getStus(int counts, int... a) {

        List<Integer> list = new ArrayList<>();
        for (int i : a) {
            list.add(i);
        }


        Map<Integer, Integer> map = new HashMap<>();
        int count = 1;
        for (int i : a) {
            count = map.containsKey(i) ? map.get(i) + 1 : 1;
            map.put(i, count);
        }
        Set<Integer> integers = map.keySet();

        System.out.println();
        for (int i : integers) {
            if (map.get(i) > counts) {
                for (int j = 0; j < map.get(i); j++) list.remove((Integer) i);
                list.remove((Integer) i);
            }
        }
        for (Integer i : list) {
            System.out.print(i + " ");
        }

    }

}

public class Main {
  public static void main(String[] args) {
    for (String s : args) {
      if ("B".equals(s)) {
        continue;
      } else if (s == new String("D")) {
        continue;
      }
      System.out.print(s);
      System.out.print(",");
      if ("E".equals(s)) {
        break;
      }
    }
    System.out.println();
  }
}
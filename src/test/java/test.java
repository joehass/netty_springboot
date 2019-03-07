public class test {

    public static void main(String[] args) {

        String str = "1,2,3";
        String[] str1 = str.split(",");
        String str2 = "";
        for (int i = 0;i < str1.length;i++){
            str2 += str1[i];
        }
        System.out.println(str1[0]);
    }

}
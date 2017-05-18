package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class MyClass {
    private static Queue <InputStream>isQueue=new LinkedList<>();
    private static Queue <OutputStream>osQueue=new LinkedList<>();
    public static void main(String []s) {
        Scanner sc=new Scanner(System.in);
        String line=sc.nextLine();
        char[]chars=line.toCharArray();
        for(int i=0;i<chars.length;i++)
        {
            if(chars[i]>'z'||chars[i]<'a')
            {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static ArrayList<Integer> getFibonacci()
    {
        ArrayList<Integer>arrayList=new ArrayList<>();
        arrayList.add(1);
        arrayList.add(1);
        int first=1,second=1,sum=0;
        for(int i=2;i<11;i++)
        {
            sum=first+second;
            first=second;
            second=sum;
            arrayList.add(sum);
        }
        return arrayList;
    }
}

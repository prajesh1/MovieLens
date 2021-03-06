
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
public class UsersOnMyAge {
        
 public static class Map extends Mapper<LongWritable, Text, IntWritable, IntWritable> //KeyIn, ValueIn, KeyOut,ValueOut
 {
    private final static IntWritable one = new IntWritable(1);
    private IntWritable age = new IntWritable();
        
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String lines = value.toString();
        String delim = "::";        
        String[] line = lines.split(delim);
        for(int i=0;i<line.length;i++)
        {
        		age.set(Integer.parseInt(line[2]));//Age at 3 column
	            context.write(age, one);
	    }
        
    }
 } 
 public static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
	    @Override			
	    public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) 
	      throws IOException, InterruptedException {
	    	
	        int sum = 0;
	        for (IntWritable val : values) {
	            sum += val.get();
	        }
	        if(key.get()==25)
	        context.write(key, new IntWritable(sum));
	    }
	 }     

        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
        Job job = new Job(conf, "usersofmyage");     
    
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    job.setJarByClass(UsersOnMyAge.class);
    job.waitForCompletion(true);

 }
        
}

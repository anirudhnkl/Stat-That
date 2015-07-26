
import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

/**
* @author Anirudh Balasubramaniam
*/
public class Grade
{
	private String text;
	private ArrayList<String> gradeList;
	private ArrayList<Type> types = new ArrayList<Type>();
	private Scanner keyboard = new Scanner(System.in);

	public static void main(String[] args)
	{
		Grade grade = new Grade();
		grade.run();
	}

	public Grade()
	{
		text = "";
		gradeList = new ArrayList<String>();
	}

	public void run()
	{
		try
		{
		  getText();
		}
		catch(IOException e)
		{
		  e.printStackTrace();
		}
		separate();
		ask();
	}

	public String askUser(String ask)
	{
		System.out.print(ask+"\t"); 
		return keyboard.nextLine();
	}


	public void getText() throws IOException 
	{
		
		String fileName = askUser("Enter the file name with your grades");
		Scanner input = null;

		try
		{
			input = new Scanner(new File(fileName+".txt"));
		} catch(FileNotFoundException e)
		{
			System.err.println("ERROR: Cannot open " +
				fileName + " for reading.");
			System.exit(1);
		}

		while(input.hasNext())
   		{
   			gradeList.add(input.nextLine());
   		}
   		input.close();
	}

	public void separate()
	{
		String [] list = new String[10];
		String str = "";
		boolean inTypes = false;
		ArrayList<String> result = new ArrayList<>();
		HashSet<String> set = new HashSet<>();
		ArrayList<String> typeNames = new ArrayList<String>(gradeList.size());

		for(int y = 0; y < gradeList.size(); y++)
		{
			str = gradeList.get(y);
			list = str.split("\\t");
			typeNames.add(list[0]);
		}


		for (String item : typeNames) 
		{
		    if (!set.contains(item)) 
		    {
				result.add(item);
				set.add(item);
		    }
		}

		for(int w = 0; w < result.size(); w++)
			types.add(new Type(result.get(w), findWeightage(result.get(w))));

		for(int i = 0; i < gradeList.size(); i++)
		{
			str = gradeList.get(i);
			list = str.split("\\t");
			double total;
			if(list[4].indexOf('=') != -1)
			{
				total = Double.parseDouble(list[4].substring(list[4].indexOf('/') + 2,list[4].indexOf('=') - 1));
			}
			else
			{
				total = Double.parseDouble(list[5].substring(0,list[5].indexOf('=') - 1));
			}
			findType(list[0]).addAssignment(list[1], Double.parseDouble(list[3]), total, list[2]);
		}
	}

	public Type findType(String s)
	{
	    for(Type t : types)
	    {
	        if(t.getName().equals(s))
	        {
	            return t;
	        }
	    }
	    return null;
	}

	public static double round(double value, int places) 
	{
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	public void ask()
	{
		System.out.println("What should be done next:");
		System.out.println("Enter 1 - to print\nEnter 2 - to add new assignment to get desired grade\nEnter 3 - to find out to calculate needed grade for a certain category to get desired grade\nEnter 4 - to find total points of in a category\n\n");
		int choice = Integer.parseInt(keyboard.next());
		switch(choice)
		{
			case 1: print(); break;
			case 2:
			case 3: runCalculation(choice); break;
			case 4: System.out.println(findTotal()); break;
		}
	}

	public double findWeightage(String type)
	{
		return Double.parseDouble(askUser("What is the weightage for " + type + "?"));
	}

	public void print()
	{
		System.out.println(types.get(0).getTotal() + " " + types.get(0).getTotalScore());
	}

	public void runCalculation(int choice)
	{
		double desiredScore = Double.parseDouble(askUser("What is your desired score?"));
		String type = "";
		double neededGrade = 0.0;

		if(choice == 2)
		{
			type = askUser("What type will this assignment be?");
			double total = Double.parseDouble(askUser("What is the total points availabe for this test?"));
			neededGrade = calculateNeededGradeAssignment(findType(type), desiredScore, total);
		}

		else
		{
			type = askUser("What category will this be calculated for?");
			neededGrade = calculateNeededGradeCategory(findType(type), desiredScore);
		}
		System.out.println(neededGrade);
	}

	public double calculateNeededGradeCategory(Type t, double desiredScore)
	{
		double weightage = t.getWeightage();
		double neededGrade = (desiredScore - t.getPercent()*(1-weightage))/weightage;
		return neededGrade;
	}

	public double calculateNeededGradeAssignment(Type t, double desiredScore, double total)
	{
		double typeGrade = calculateNeededGradeCategory(t, desiredScore);
		double neededScore = (typeGrade)*t.getTotal()-(t.getTotalScore()+total);
		return neededScore;
	}

	public double findTotal()
	{
		//String type = askUser("What type do you want your total points for?");
		//String type = "Assignment";
		int sum = 0;
		//if(type.equals("all"))
		//{
			for(Type t : types)
			{	
				sum += t.getTotalScore();
			}
			return sum;
		/*}

		else
		{
			Type t = findType(type);
			return t.getTotal();
		}*/
	}
}



/*
	//per Type
    public double calculateNeededGrade(Type t, double desiredScore)	//returns percentage needed for this type overall
	{
		double weightage = t.getWeightage();
		double neededGrade = (desiredScore-calculateOverall()*(1-weightage))/weightage;
		return neededGrade;
	}
    
    //only one assignment
	public void calculateNeededGrade(Type t,double desiredScore, double total)	//if you have the total points. returns the numPoints needed
	{
		   double typeGrade = calculateNeededGrade(t, desiredScore);//the required grade in the overall type
		   double neededScore = (typeGrade)*t.getTotal()-(t.getTotalScore()+total);
		   return neededScore;
	}
	*/

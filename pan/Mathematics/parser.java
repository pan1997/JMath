package pan.Mathematics;

import java.util.ArrayList;

/**
 * Created by pankaj on 08-06-2014.
 */
public class parser
{
    static final int DELIM=1;
    static final int NUM=2;
    static final int TEXT=3;
    String exp;
    String tok;
    int tt;
    int i;
    void next()
    {
        while(i<exp.length()&&exp.charAt(i)==' ')i++;
        if(i==exp.length())
        {
            tok="";
            tt=DELIM;
            return;
        }
        char ch=exp.charAt(i);
        if(isDelimiter(ch))
        {
            tok=ch+"";
            i++;
            tt=DELIM;
        }
        else if(ch<='9'&&ch>='0'||ch=='.')
        {
            tok="";
            while(i<exp.length()&&!isDelimiter(exp.charAt(i)))
                tok=tok+exp.charAt(i++);
            tt=NUM;
        }
        else
        {
            tok="";
            while(i<exp.length()&&!isDelimiter(exp.charAt(i)))
                tok=tok+exp.charAt(i++);
            tt=TEXT;
        }
        //System.out.println(tok);
    }
    expression parse(String s)
    {
        exp=s;
        i=0;
        next();
        return plusL();
    }
    expression plusL()
    {
        expression e=intoL();
        if(tok.equals("+")||tok.equals("-"))
        {
            ArrayList<expression> an=new ArrayList<>();
            an.add(e);
            while(tok.equals("+")||tok.equals("-"))
                if(tok.equals("+"))
                {
                    next();
                    an.add(intoL());
                }
                else if(tok.equals("-"))
                {
                    next();
                    an.add(new into(new num(-1),intoL()));
                }
            expression[] en=new expression[an.size()];
            an.toArray(en);
            return new plus(en);
        }
        else return e;
    }
    expression intoL()
    {
        expression e=powL();
        if(tok.equals("*")||tok.equals("/"))
        {
            ArrayList<expression> an=new ArrayList<>();
            an.add(e);
            while(tok.equals("*")||tok.equals("/"))
                if(tok.equals("*"))
                {
                    next();
                    an.add(powL());
                }
                else if(tok.equals("/"))
                {
                    next();
                    an.add(new pow(powL(),new num(-1)));
                }
            expression[] en=new expression[an.size()];
            an.toArray(en);
            return new into(en);
        }
        else return e;
    }
    expression powL()
    {
        expression e=bracketL();
        if(tok.equals("^"))
        {
            next();
            return new pow(e,bracketL());
        }
        else return e;
    }
    expression bracketL()
    {
        if(tok.equals("("))
        {
            //System.out.println("BR open");
            next();
            expression e=plusL();
            //System.out.println("BR closed");
            if(!tok.equals(")"))
                System.out.println("Missing ) "+i+" "+tok);
            else next();
            return e;
        }
        else return atomicL();
    }
    expression atomicL()
    {
        expression ans=null;
        if(tt==NUM) {
            ans = new num(Double.parseDouble(tok));
            next();
        }
        else if(tt==TEXT)
            switch (tok)
            {
                case "sin":next();ans=new sin(bracketL());break;
                case "cos":next();ans=new cos(bracketL());break;
                case "tan":next();ans=new tan(bracketL());break;
                case "sinh":next();ans=new sinh(bracketL());break;
                case "cosh":next();ans=new cosh(bracketL());break;
                case "tanh":next();ans=new tanh(bracketL());break;
                case "ln":next();ans=new ln(bracketL());break;
                case "exp":next();ans=new exp(bracketL());break;
                case "asin":next();ans=new asin(bracketL());break;
                case "acos":next();ans=new acos(bracketL());break;
                case "atan":next();ans=new atan(bracketL());break;
                default:ans = new variable(tok);next();
            }
        else System.out.println("ERROR");
        return ans;
    }
    boolean isDelimiter(char ch)
    {
        return " +-*/%^([)]{}".indexOf(ch)>0;
    }
}

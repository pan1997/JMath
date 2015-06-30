package pan.Mathematics;
import java.util.Map;
/**
 * Created by pankaj on 09-06-2014.
 */
public class Numerical
{
    private Numerical()
    {
    }
    static double root(expression y,String v,Map<String,expression> sbs,double seed)
    {
        num x=new num(seed);
        expression y_=y.differentiate(sbs,v).simplify(sbs);
        //System.out.println(y+" diff wrt "+v+"="+y_);
        sbs.put(v,x);
        double t;
        for(int i=0;i<1000;i++) {
            t=y.eval(sbs);
            if(t==0)
            {
                break;
            }
            else x.d-=t/y_.eval(sbs);
        }
        sbs.remove(v);
        return x.d;
    }
    static double[] root(expression[] y,String[] v,Map<String,expression>sbs,double[] seed)
    {
        if(y.length!=v.length||y.length!=seed.length)
            return null;
        Matrix guess=new Matrix(v.length,1);
        for(int i=0;i<seed.length;i++)
            guess.m[i][0]=seed[i];
        Matrix HS=new Matrix(seed.length,seed.length);

        expression[][] hs=new expression[v.length][v.length];
        for(int i=0;i<hs.length;i++)
            for(int j=0;j<hs.length;j++)
                hs[i][j]=y[i].differentiate(sbs,v[i]).simplify(sbs);
        num[] x=new num[seed.length];
        for(int i=0;i<x.length;i++) {
            x[i] = new num(seed[i]);
            sbs.put(v[i],x[i]);
        }
        boolean flag;
        for(int i=0;i<500;i++)
        {
            for(int k=0;k<HS.m.length;k++)
                for(int l=0;l<HS.m[k].length;l++)
                    HS.m[k][l]=hs[k][l].eval(sbs);
            guess=HS.inverse().multiply(guess);
            flag=false;
            for(int k=0;k<guess.m.length;k++)
                if(guess.m[k][0]!=x[k].d)
                    flag=true;
            if(!flag)
                break;
            for(int k=0;k<x.length;k++)
                x[k].d=guess.m[k][0];
        }
        for(int i=0;i<x.length;i++)
            seed[i]=x[i].d;
        return seed;
    }

}

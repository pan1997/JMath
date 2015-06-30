package pan.Mathematics;

/**
 * Created by pankaj on 11-06-2014.
 */
public class Matrix
{
    double[][] m;
    Matrix(int i,int j)
    {
        m=new double[i][j];
    }
    Matrix(double[][] d)
    {
        m=d;
    }
    Matrix add(Matrix b)
    {
        if(b.m.length!=m.length||b.m[0].length!=m[0].length)
            return null;
        Matrix ans=new Matrix(m.length,m[0].length);
        for(int i=0;i<m.length;i++)
            for(int j=0;j<m[i].length;j++)
                ans.m[i][j]=m[i][j]+b.m[i][j];
        return ans;
    }
    public String toString()
    {
        String ans="";
        for(int i=0;i<m.length;i++) {
            ans=ans+"|";
            for (int j = 0; j < m[i].length; j++)
                ans=ans+m[i][j]+",";
            ans=ans+"|\n";
        }
        return ans;
    }
    Matrix subtract(Matrix b)
    {
        if(b.m.length!=m.length||b.m[0].length!=m[0].length)
            return null;
        Matrix ans=new Matrix(m.length,m[0].length);
        for(int i=0;i<m.length;i++)
            for(int j=0;j<m[i].length;j++)
                ans.m[i][j]=m[i][j]-b.m[i][j];
        return ans;
    }
    static Matrix identity(int n)
    {
        Matrix ans=new Matrix(n,n);
        for(int i=0;i<n;i++)
            ans.m[i][i]=1;
        return ans;
    }
    private void rowTransformation(int from,double scale,int to)
    {
        for(int i=0;i<m[from].length;i++)
            m[to][i]-=m[from][i]*scale;
    }
    private void scale(int r,double sc)
    {
        for(int i=0;i<m[r].length;i++)
            m[r][i]/=sc;
    }
    Matrix inverse()
    {
        if(m.length!=m[0].length)
            return null;
        Matrix ans=new Matrix(m.length,m.length*2);
        for(int i=0;i<m.length;i++) {
            for (int j = 0; j < m[i].length; j++)
                ans.m[i][j] = m[i][j];
            ans.m[i][m.length+i]=1;
        }
        for(int i=0;i<m.length;i++) {
            ans.scale(i,ans.m[i][i]);
            for (int k = i + 1; k < m.length; k++)
                ans.rowTransformation(i, ans.m[k][i], k);
        }

        for(int i=m.length-1;i>0;i--)
            for(int j=i-1;j>=0;j--)
                ans.rowTransformation(i,ans.m[j][i],j);

        Matrix inverse=new Matrix(m.length,m.length);
        for(int i=0;i<m.length;i++)
            for(int j=0;j<m[i].length;j++)
                inverse.m[i][j]=ans.m[i][m.length+j];
        return inverse;
    }
    double determinant()
    {
        if(m.length!=m[0].length)
            return -1;
        Matrix ans=new Matrix(m.length,m.length);
        for(int i=0;i<m.length;i++) {
            for (int j = 0; j < m[i].length; j++)
                ans.m[i][j] = m[i][j];
        }
        for(int i=0;i<m.length;i++) {
            for (int k = i + 1; k < m.length; k++)
                ans.rowTransformation(i, ans.m[k][i]/ans.m[i][i], k);
        }
        double det=1;
        for(int i=0;i<m.length;i++)
            det*=ans.m[i][i];
        return det;
    }
    Matrix multiply(Matrix b)
    {
        if(m[0].length!=b.m.length)
            return null;
        Matrix ans=new Matrix(m.length,b.m[0].length);
        for(int i=0;i<ans.m.length;i++)
            for(int j=0;j<ans.m[i].length;j++)
                for(int k=0;k<m[0].length;k++)
                    ans.m[i][j]+=m[i][k]*b.m[k][j];
        return ans;
    }
}

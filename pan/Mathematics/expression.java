package pan.Mathematics;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by pankaj on 08-06-2014.
 */
public abstract class expression
{
    double w;
    abstract double eval(Map<String,expression> sbs);
    abstract expression differentiate(Map<String,expression>sbs,String v);
    abstract boolean fixed();
    abstract boolean dependsOn(Map<String,expression>sbs,String v);
    abstract boolean dependsOn(Map<String,expression>sbs,Set<String> s);
    abstract expression simplify(Map<String,expression> sbs);
    abstract int prescendence();
    abstract Group draw(double x,double y,double size,String Fname);
}
abstract class compoundExpression extends expression
{
    expression[] children;
    compoundExpression(expression... s)
    {
        children=s;
    }

    @Override
    public String toString()
    {
        String ans=this.getClass().getSimpleName()+"("+(children.length>0?children[0]:"");
        for(int i=1;i<children.length;i++)
            ans=ans+","+children[i];
        return ans+")";
    }
    @Override
    boolean dependsOn(Map<String,expression>sbs,String v)
    {
        for(expression e:children)
            if(e.dependsOn(sbs,v))
                return true;
        return false;
    }
    @Override
    boolean dependsOn(Map<String,expression>sbs,Set<String > s)
    {
        for(expression e:children)
            if(e.dependsOn(sbs,s))
                return true;
        return false;
    }
    @Override
    boolean fixed()
    {
        for(expression e:children)
            if(!e.fixed())
                return false;
        return true;
    }
    expression simplify(Map<String,expression>sbs)
    {
        boolean fx=true;
        for(int i=0;i<children.length;i++) {
            children[i] = children[i].simplify(sbs);
            if(!children[i].fixed())
                fx=false;
        }
        if(fx)
            return new num(eval(sbs));
        return this;
    }

    @Override
    Group draw(double x, double y,double size,String f) {
        double px=x;
        Group g=new Group();
        Text t=new Text(this.getClass().getSimpleName());
        t.setFill(Color.BLUE);
        t.setFont(Font.font(f, size));
        t.setX(x);
        x+=t.getLayoutBounds().getWidth();
        t.setY(y);
        g.getChildren().add(t);
        t=new Text("(");
        t.setFill(Color.BLACK);
        t.setFont(Font.font(f, size));
        t.setX(x);
        x+=t.getLayoutBounds().getWidth();
        t.setY(y);
        g.getChildren().add(t);
        Group g1=children[0].draw(x,y,size,f);
        x+=children[0].w;
        g.getChildren().add(g1);
        for(int i=1;i<children.length;i++) {
            t=new Text(",");
            t.setFill(Color.BLACK);
            t.setFont(Font.font(f, size));
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().add(t);
            g1 = children[i].draw(x, y, size, f);
            x+=children[i].w;
            g.getChildren().add(g1);
        }
        t=new Text(")");
        t.setFill(Color.BLACK);
        t.setFont(Font.font(f,size));
        t.setX(x);
        x+=t.getLayoutBounds().getWidth();
        t.setY(y);
        g.getChildren().add(t);
        w=x-px;
        return g;
    }

    int prescendence()
    {
        return Integer.MAX_VALUE;
    }
}
class num extends expression
{
    double d;
    num(double s)
    {
        d=s;
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return d;
    }
    @Override
    public String toString()
    {
        return d+"";
    }
    @Override
    expression differentiate(Map<String ,expression>sbs,String v) {
        return new num(0);
    }

    @Override
    boolean fixed() {
        return true;
    }

    @Override
    boolean dependsOn(Map<String,expression>sbs,String v) {
        return false;
    }

    @Override
    boolean dependsOn(Map<String,expression>sbs,Set<String> s) {
        return false;
    }

    @Override
    expression simplify(Map<String, expression> sbs) {
        return this;
    }

    int prescendence()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    Group draw(double x, double y, double size, String f) {
        Text t=new Text(""+d);
        t.setFill(Color.BLACK);
        t.setFont(Font.font(f,size));
        t.setX(x);
        t.setY(y);
        w=t.getLayoutBounds().getWidth();
        return new Group(t);
    }
}
class variable extends expression
{
    String s;
    variable(String v)
    {
        s=v;
    }
    @Override
    double eval(Map<String, expression> sbs) {
        expression e=sbs.get(s);
        return e==null?0:e.eval(sbs);
    }

    @Override
    public String toString()
    {
        return s;
    }

    @Override
    Group draw(double x, double y, double size, String f) {
        Text t=new Text(s);
        t.setFill(Color.BLUEVIOLET);
        t.setFont(Font.font(f,size));
        t.setX(x);
        t.setY(y);
        w=t.getLayoutBounds().getWidth();
        return new Group(t);
    }
    @Override
    expression differentiate(Map<String,expression>sbs,String v) {
        if(s.equals(v))
            return new num(1);
        if(sbs.containsKey(s))
            return sbs.get(s).differentiate(sbs,v);
        return new num(0);
    }

    @Override
    boolean fixed() {
        return false;
    }

    @Override
    boolean dependsOn(Map<String,expression>sbs,String v) {
        if(v.equals(s)) return true;
        if(sbs.containsKey(s)) return sbs.get(s).dependsOn(sbs,v);
        return false;
    }

    @Override
    boolean dependsOn(Map<String,expression>sbs,Set<String> v) {
        if(v.contains(s)) return true;
        if(sbs.containsKey(s)) return sbs.get(s).dependsOn(sbs,v);
        return false;
    }

    @Override
    expression simplify(Map<String, expression> sbs) {
        if(sbs.containsKey(s))
            return sbs.get(s).simplify(sbs);
        else return this;
    }

    int prescendence()
    {
        return Integer.MAX_VALUE;
    }
}
class sin extends compoundExpression
{
    sin(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.sin(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new cos(children[0]),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class cos extends compoundExpression
{
    cos(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.cos(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new num(-1),new sin(children[0]),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class into extends compoundExpression
{
    into(expression... e)
    {
        super(e);
    }
    @Override
    double eval(Map<String, expression> sbs)
    {
        double ans=1;
        for(expression e:children)
            ans*=e.eval(sbs);
        return ans;
    }

    @Override
    expression simplify(Map<String,expression>sbs)
    {
        num n=new num(1);
        ArrayList<expression> list=new ArrayList<>();
        for(int i=0;i<children.length;i++)
        {
            children[i]=children[i].simplify(sbs);
            if(children[i].fixed())
                n.d*=children[i].eval(sbs);
            else if(children[i] instanceof into)
            {
                into p=(into)children[i];
                for(expression e:p.children)
                    if(e.fixed())
                        n.d*=e.eval(sbs);
                    else list.add(e);
            }
            else list.add(children[i]);
        }
        if(n.d==0)
            return n;
        if(n.d!=1)
            list.add(n);
        if(list.size()==1)
            return list.get(0);
        expression[] ee=new expression[list.size()];
        list.toArray(ee);
        return new into(ee);
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        ArrayList<expression> t=new ArrayList<>();
        for(int i=0;i<children.length;i++)
            if(children[i].dependsOn(sbs,v))
            {
                expression[] e1=new expression[children.length];
                for(int j=0;j<children.length;j++)
                    if(i==j)
                        e1[j]=children[j].differentiate(sbs,v);
                    else e1[j]=children[j];
                t.add(new into(e1));
            }
        if(t.size()==1)
            return t.get(0);
        expression[] ee=new expression[t.size()];
        t.toArray(ee);
        return new plus(ee);
    }

    @Override
    public String toString() {
        String ans=children[0].prescendence()>5000?children[0].toString():"("+children[0]+")";
        for(int i=1;i<children.length;i++)
            ans+="*"+(children[i].prescendence()>5000?children[i]:"("+children[i]+")");
        return ans;
    }

    @Override
    Group draw(double x, double y, double size, String f) {
        Group g=new Group();
        double px=x;
        Text t;
        if(children[0].prescendence()>5000)
        {
            g.getChildren().add(children[0].draw(x,y,size,f));
            x+=children[0].w;
        }
        else
        {
            t=new Text("(");
            t.setFont(Font.font(f, size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().addAll(t,children[0].draw(x,y,size,f));
            x+=children[0].w;
            t=new Text(")");
            t.setFont(Font.font(f,size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().add(t);
        }
        for(int i=1;i<children.length;i++) {
            t=new Text("*");
            t.setFont(Font.font(f,size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().add(t);
            if (children[i].prescendence() > 5000) {
                g.getChildren().add(children[i].draw(x, y, size, f));
                x += children[i].w;
            } else {
                t = new Text("(");
                t.setFont(Font.font(f, size));
                t.setFill(Color.BLACK);
                t.setX(x);
                x += t.getLayoutBounds().getWidth();
                t.setY(y);
                g.getChildren().addAll(t, children[i].draw(x, y, size, f));
                x += children[i].w;
                t = new Text(")");
                t.setFont(Font.font(f, size));
                t.setFill(Color.BLACK);
                t.setX(x);
                x += t.getLayoutBounds().getWidth();
                t.setY(y);
                g.getChildren().add(t);
            }
        }
        w=x-px;
        return g;
    }

    int prescendence()
    {
        return 5000;
    }
}
class plus extends compoundExpression
{
    plus(expression... e)
    {
        super(e);
    }
    @Override
    double eval(Map<String, expression> sbs)
    {
        double ans=0;
        for(expression e:children)
            ans+=e.eval(sbs);
        return ans;
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        ArrayList<expression> an=new ArrayList<>();
        for(expression e:children)
            if(e.dependsOn(sbs,v))
                an.add(e.differentiate(sbs,v));
        if(an.size()==0)
            return new num(0);
        expression[] er=new expression[an.size()];
        an.toArray(er);
        return new plus(er);
    }
    @Override
    expression simplify(Map<String,expression>sbs) {
        num n = new num(0);
        ArrayList<expression> list = new ArrayList<>();
        for (int i = 0; i < children.length; i++) {
            children[i] = children[i].simplify(sbs);
            if (children[i].fixed())
                n.d += children[i].eval(sbs);
            else if (children[i] instanceof plus) {
                plus p = (plus) children[i];
                for (expression e : p.children)
                    if (e.fixed())
                        n.d += e.eval(sbs);
                    else list.add(e);
            } else list.add(children[i]);
        }
        if (n.d != 0)
            list.add(n);
        if(list.size()==1)
            return list.get(0);
        expression[] ee = new expression[list.size()];
        list.toArray(ee);
        return new plus(ee);
    }

    @Override
    public String toString() {
        String ans=children[0].prescendence()>1000?children[0].toString():"("+children[0]+")";
        for(int i=1;i<children.length;i++)
            ans+="+"+(children[i].prescendence()>1000?children[i]:"("+children[i]+")");
        return ans;
    }

    @Override
    Group draw(double x, double y, double size, String f) {
        Group g=new Group();
        double px=x;
        Text t;
        if(children[0].prescendence()>1000)
        {
            g.getChildren().add(children[0].draw(x,y,size,f));
            x+=children[0].w;
        }
        else
        {
            t=new Text("(");
            t.setFont(Font.font(f, size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().addAll(t,children[0].draw(x,y,size,f));
            x+=children[0].w;
            t=new Text(")");
            t.setFont(Font.font(f,size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().add(t);
        }
        for(int i=1;i<children.length;i++) {
            t=new Text("+");
            t.setFont(Font.font(f,size));
            t.setFill(Color.BLACK);
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().add(t);
            if (children[i].prescendence() > 1000) {
                g.getChildren().add(children[i].draw(x, y, size, f));
                x += children[i].w;
            } else {
                t = new Text("(");
                t.setFont(Font.font(f, size));
                t.setFill(Color.BLACK);
                t.setX(x);
                x += t.getLayoutBounds().getWidth();
                t.setY(y);
                g.getChildren().addAll(t, children[i].draw(x, y, size, f));
                x += children[i].w;
                t = new Text(")");
                t.setFont(Font.font(f, size));
                t.setFill(Color.BLACK);
                t.setX(x);
                x += t.getLayoutBounds().getWidth();
                t.setY(y);
                g.getChildren().add(t);
            }
        }
        w=x-px;
        return g;
    }

    int prescendence()
    {
        return 1000;
    }
}
class pow extends compoundExpression
{
    pow(expression r1,expression r2)
    {
        super(r1,r2);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.pow(children[0].eval(sbs), children[1].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        //if(!dependsOn(sbs,v))
        //    return new num(0);
        return new into(this,new plus(new into(children[1].differentiate(sbs,v),new ln(children[0])),new into(children[1],children[0].differentiate(sbs,v),new pow(children[0],new num(-1)))));
    }

    @Override
    public String toString() {
        return (children[0].prescendence()>10000?children[0]:"("+children[0]+")")+"^"+(children[1].prescendence()>10000?children[1]:"("+children[1]+")");
    }

    @Override
    Group draw(double x, double y, double size, String f) {
        double px=x;
        Group g=new Group();
        if(children[0].prescendence()>10000)
        {
            g.getChildren().add(children[0].draw(x,y,size,f));
            x+=children[0].w;
        }
        else
        {
            Text t=new Text("(");
            t.setFont(Font.font(f,size));
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().addAll(t);
            g.getChildren().add(children[0].draw(x,y,size,f));
            x+=children[0].w;
            t=new Text(")");
            t.setFont(Font.font(f,size));
            t.setX(x);
            x+=t.getLayoutBounds().getWidth();
            t.setY(y);
            g.getChildren().addAll(t);
        }


        g.getChildren().add(children[1].draw(x,y-size/3,size*2/3,f));
        x+=children[1].w;
        w=x-px;
        return g;
    }

    int prescendence()
    {
        return 10000;
    }
}
class tan extends compoundExpression
{
    tan(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.tan(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new pow(new cos(children[0]),new num(-2)),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class exp extends compoundExpression
{
    exp(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.exp(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new exp(children[0]),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class sinh extends compoundExpression
{
    sinh(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.sinh(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new cosh(children[0]),children[0].differentiate(sbs, v));
        return new num(0);
    }
}
class cosh extends compoundExpression
{
    cosh(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.cosh(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new sinh(children[0]),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class ln extends compoundExpression
{
    ln(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.log(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs, v))
            return new into(new pow(children[0],new num(-1)),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class tanh extends compoundExpression
{
    tanh(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.tanh(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new pow(new cosh(children[0]),new num(-2)),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class asin extends compoundExpression
{
    asin(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.asin(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new pow(new plus(new num(1),new into(new num(-1),new pow(children[0],new num(2)))),new num(-0.5)),children[0].differentiate(sbs,v));
        return new num(0);
    }
}
class acos extends compoundExpression
{
    acos(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.acos(children[0].eval(sbs));
    }

    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new pow(new plus(new num(1),new into(new num(-1),new pow(children[0],new num(2)))),new num(-0.5)),children[0].differentiate(sbs,v),new num(-1));
        return new num(0);
    }
}
class atan extends compoundExpression
{
    atan(expression r)
    {
        super(r);
    }
    @Override
    double eval(Map<String, expression> sbs) {
        return Math.atan(children[0].eval(sbs));
    }



    @Override
    expression differentiate(Map<String, expression> sbs, String v)
    {
        if(children[0].dependsOn(sbs,v))
            return new into(new pow(new plus(new num(1),new pow(children[0],new num(2))),new num(-1)),children[0].differentiate(sbs,v));
        return new num(0);
    }
}

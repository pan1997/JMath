package pan.Mathematics;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.*;


/**
 * Created by pankaj on 16-06-2014.
 */
public class Main extends Application
{
    static final int fontSize=16;
    BorderPane root;
    VBox right;
    TextFlow txt;
    ToolBar toolBar;
    MenuBar bar;
    Font f1,f2,f3;
    Menu file,edit,help;
    TextField input;
    ScrollPane sp;
    VBox rt;
    @Override
    public void init()
    {
        rt=new VBox();
        root=new BorderPane();
        txt=new TextFlow();
        right=new VBox();
        toolBar=new ToolBar();
        bar=new MenuBar();
        file=new Menu("File");
        edit=new Menu("Edit");
        help=new Menu("Help");
        bar.getMenus().addAll(file, edit, help);
        MenuItem exit=new MenuItem("Exit");
        file.getItems().add(exit);
        exit.setOnAction((e) -> Platform.exit());
        f1=Font.font("Consolas",fontSize);
        f2=Font.font("Calibri",fontSize);
        f3=Font.font("Cambria",fontSize);
        input=new TextField();
        input.setFont(f3);
        root.setRight(right);
        sp=new ScrollPane(txt);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        //sp.prefViewportHeightProperty().bind(sp.heightProperty());
        root.setCenter(sp);
        root.setTop(toolBar);
        txt.getChildren().add(input);
        txt.setLineSpacing(5);
        sp.prefHeightProperty().bind(rt.heightProperty().subtract(100));
        input.setOnAction(this::parseCommand);
        input.prefWidthProperty().bind(txt.widthProperty());
        txt.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
        print("Mathematics version 1", Color.DARKVIOLET, f1);
        print("By Pankaj Kumar Yadav", Color.DARKVIOLET, f2);
        print("\n",Color.BLANCHEDALMOND,f2);
        p=new parser();
        bindings=new HashMap<>();
        bindings.put("e",new num(Math.E));
        bindings.put("pi",new num(Math.PI));
        rt.getChildren().addAll(bar,root);
    }
    parser p;
    Map<String,expression> bindings;
    void parseCommand(ActionEvent event)
    {
        String c=input.getText().trim();
        input.clear();
        print(c,Color.DARKGREY,f3);
        if(c.equals("exit"))
            Platform.exit();
        else if(c.startsWith("solve")||c.startsWith("find"))
        {
            ArrayList<String> arg=getArgs(c);
            System.out.println(arg);
            double r=Numerical.root(p.parse(arg.get(0)).simplify(bindings),arg.get(1),bindings, Double.parseDouble(arg.get(2)));
            print(arg.get(1)+"->"+r,Color.BLACK,f3);
            bindings.put("ans",new num(r));
        }
        else if(c.startsWith("differentiate"))
        {
            ArrayList<String> arg=getArgs(c);
            System.out.println(arg);
            expression ans=p.parse(arg.get(0)).simplify(bindings).differentiate(bindings,arg.get(1)).simplify(bindings);
            print(ans,f3);
            bindings.put("ans",ans);
        }
        else if(c.startsWith("simplify"))
        {
            bindings.put("ans",p.parse(getArgs(c).get(0)).simplify(bindings));
            print(bindings.get("ans"),f3);
        }
        else if(c.contains("="))
        {
            String v=c.substring(0,c.indexOf("=")).trim();
            String exp=c.substring(c.indexOf("=")+1).trim();
            expression e;
            bindings.put(v,e=p.parse(exp).simplify(bindings));
            bindings.put("ans",e);
            print(e, f3);
        }
        else
        {
            expression e=p.parse(c).simplify(bindings);
            bindings.put("ans",e);
            print(e,f3);
        }

    }

    ArrayList<String> getArgs(String s)
    {
        int i=s.indexOf('[');
        if(i==-1)
            i=s.indexOf('(');
        String w;
        ArrayList<String> ans=new ArrayList<>();
        int nb=0;
        while(i<s.length())
        {
            w="";
            nb=0;
            for(i++;i<s.length();i++)
                if(",{}()[]".indexOf(s.charAt(i))==-1)
                    w+=s.charAt(i);
                else if("({[".indexOf(s.charAt(i))!=-1)
                {
                    w+=s.charAt(i);
                    nb++;
                }
                else
                {
                    nb--;
                    if(nb==-1)
                        break;
                    else w+=s.charAt(i);
                }
            ans.add(w);
        }
        return ans;
    }
    void print(String s,Paint p,Font font)
    {
        Text t=new Text("   "+s+"\n");
        t.setFill(p);
        t.setFont(font);
        txt.getChildren().add(txt.getChildren().indexOf(input),t);
        sp.setVvalue(sp.getVmax());
    }
    void print(expression e,Font f)
    {
        Text t1=new Text("   ");
        t1.setFont(f);
        txt.getChildren().add(txt.getChildren().indexOf(input), t1);
        Group g=e.draw(0, 0, f.getSize(), f.getName());
        txt.getChildren().add(txt.getChildren().indexOf(input),g);
        t1=new Text("\n");
        t1.setFont(f);
        txt.getChildren().add(txt.getChildren().indexOf(input), t1);
        sp.setVvalue(sp.getVmax());

    }
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Mathematics");
        Scene s=new Scene(rt,500,500);
        primaryStage.setScene(s);
        primaryStage.setOnCloseRequest((e)->Platform.exit());
        primaryStage.show();
    }

    public static void main(String[] argv)
    {
        Application.launch(argv);
    }

}

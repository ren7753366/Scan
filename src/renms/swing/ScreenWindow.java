package renms.swing;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.zxing.common.StringUtils;

import renms.swing.context.ScanContext;
import renms.swing.utils.AesUtils;
import renms.swing.utils.ImageUtil;
import renms.swing.utils.httpUtil;  
  
/** 
 * 全屏显示的窗口, 按 Alt +　F4 退出 
 * @author pengranxiang 
 */  
public class ScreenWindow extends JFrame {  
    private static final long serialVersionUID = -3758062802950480258L;  
      
    private Image image;  
    private JLabel imageLabel;  
      
    //将截图的图像加载到的目标组件，用于传值  
    private JLabel targetLabel;  
      
    private int x, y, xEnd, yEnd;   //用于记录鼠标点击开始和结束的坐标  
  
    private JTextField login;
    private JTextField cnmaster;
    public ScreenWindow(JLabel targetLabel,JTextField j1,JTextField j2) throws AWTException, InterruptedException {  
        //目标组件初始化  
        this.targetLabel = targetLabel;  
        
        this.login = j1;
        this.cnmaster = j2;
        
        //取得屏幕尺寸  
        Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();  
        //取得全屏幕截图  
        image = GraphicsUtils.getScreenImage(0, 0, screenDims.width, screenDims.height);  
        //用于展示截图  
        imageLabel = new JLabel(new ImageIcon(image));  
        //当鼠标在imageLabel上时，展示为 十字形  
        imageLabel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));  
          
        createAction();  
          
        this.getContentPane().add(imageLabel);  
          
        this.setUndecorated(true);  //去掉窗口装饰   
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setVisible(true);  
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);   //窗口最大化  
    }  
      
    /** 
     * 实现监听动作 
     */  
    private void createAction() {  
        imageLabel.addMouseListener(new MouseAdapter() {  
            public void mouseClicked(MouseEvent e) {  
                if(e.getButton() == MouseEvent.BUTTON3) { //鼠标右键单击事件  
                    //退出ScreenWindow  
                    ScreenWindow.this.dispose();  
                }  
            }  
              
            public void mousePressed(MouseEvent e) {  
                x = e.getX();  
                y = e.getY();  
            }  
              
            public void mouseReleased(MouseEvent e) {  
                xEnd = e.getX();  
                yEnd = e.getY();  
                  
                //鼠标弹起时，取得鼠标起始两点组成的矩形区域的图像  
                try {  
                    //因为 xEnd 可能比  x 小 （由右网左移动）起始坐标取其中较小值，xEnd - x 取其绝对值， 同样处理y  
                    image = GraphicsUtils.getScreenImage(Math.min(x, xEnd), Math.min(y, yEnd), Math.abs(xEnd - x), Math.abs(yEnd - y));  
                } catch (AWTException e1) {  
                    e1.printStackTrace();  
                } catch (InterruptedException e1) {  
                    e1.printStackTrace();  
                }  
                  
                //为了查看截图效果，将区域截图的部分代替全屏的截图展示  
                //imageLabel.setIcon(new ImageIcon(image));  
                  
                //将所截得的图像，加载到目标组件targetLabel  
                targetLabel.setIcon(new ImageIcon(image));  
                  
                ScreenWindow.this.dispose();
                
                //识别二维码，调用mark后台代码------------------------------
                
                BufferedImage bi = ImageUtil.toBufferedImage(image);
                String result = ImageUtil.parseQRCode(bi);
                
                //获取账号和主账号值
                String loginValue = login.getText();
                String cnmasterValue = cnmaster.getText();
                if(result==null||"".equals(result)){
                	Toolkit.getDefaultToolkit().beep();
                	JOptionPane.showMessageDialog(null, "二维码扫描失败", "错误",JOptionPane.ERROR_MESSAGE);
                	return;
                }
                
                if(loginValue==null||"".equals(loginValue)||cnmasterValue==null||"".equals(cnmasterValue)){
                	Toolkit.getDefaultToolkit().beep();
                	JOptionPane.showMessageDialog(null, "输入框必填","错误", JOptionPane.ERROR_MESSAGE);
                	return;
                }
                
                //调用mark后台
                String[] strSpitCyj = result.split("\\|");
                String param = strSpitCyj[0] + "#" + cnmasterValue + "#" + loginValue+"#"+strSpitCyj[1];
                
                try {
					param = new AesUtils(ScanContext.strKey).encrypt(param);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
                
                String url = ScanContext.markUrl+param;
                String ret = httpUtil.getHttp(url);
                System.out.println(ret);
               	Toolkit.getDefaultToolkit().beep();
	           	JOptionPane.showMessageDialog(null, "扫码结果："+ret,"提示", JOptionPane.INFORMATION_MESSAGE);
	           	return;
                
            }  
        });  
          
        imageLabel.addMouseMotionListener(new MouseMotionListener() {  
            public void mouseDragged(MouseEvent e) {  
                //1. 取得鼠标的按下点和移动当前点坐标  
                xEnd = e.getX();  
                yEnd = e.getY();  
                  
                //2. 创建一个缓冲图形对象（BufferedImage） bi  
                BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);  
                Graphics2D g2d = (Graphics2D)bi.getGraphics();  
                //3. 将原始图形画到 bi 中  
                g2d.drawImage(image, 0, 0, null);     
                g2d.setColor(Color.RED);            //设置画笔颜色为红色  
                //4. 根据取得的坐标画一个矩形到 bi 中  
                //以鼠标按下点坐标和鼠标拖动的当前点坐标画矩形，作为截图区域的展示  
                //+1 与 -1 是为了防止截图时将矩形框也截进去  
                g2d.drawRect(Math.min(x, xEnd) - 1, Math.min(y, yEnd) - 1, Math.abs(xEnd - x) + 1, Math.abs(yEnd - y) + 1);   
                g2d.dispose();  
                  
                //5. 将 bi 画到屏幕上  
                Graphics g = imageLabel.getGraphics();  
                g.drawImage(bi, 0, 0, null);  
                g.dispose();  
            }  
          
            public void mouseMoved(MouseEvent e) {  
                  
            }  
        });  
    }  
      
//  public static void main(String[] args) throws AWTException, InterruptedException {  
//      new ScreenWindow();  
//  }  
}  
  
class GraphicsUtils {  
    /** 
     * 截图屏幕中制定区域的图片 
     * @param x 
     * @param y 
     * @param w 
     * @param h 
     * @return 被截部分的BufferedImage对象 
     * @throws AWTException 
     * @throws InterruptedException 
     */  
    public static BufferedImage getScreenImage(int x, int y, int w, int h) throws AWTException, InterruptedException {  
        Robot robot = new Robot();  
        BufferedImage screen = robot.createScreenCapture(new Rectangle(x, y, w, h));  
        return screen;  
    }  
}  
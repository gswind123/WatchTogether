/*
 * ×÷Õß:lightp2p@gmail.com
 * ÍøÕ¾:http://hi.baidu.com/mqlayer
 */

package com.player;

import java.awt.Rectangle;
import java.io.Serializable;

public class Config implements Serializable{
	private static final long serialVersionUID = 1237683979538355923L;
	Rectangle playerBound=new Rectangle(500,500,600,400);
	String lastOpenPath;
	String lastPlayPath="";

}

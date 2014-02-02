/*
 * OrderListTrasnferable.java
 * Copyright (C) 2002 Dolphin Project. All rights reserved.
 * Copyright (C) 2003,2004 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *	
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *	
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.client;

import java.awt.datatransfer.*;
import java.io.*;
     
/**
 * Transferable class of the PTrain.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */ 
public final class OrderListTransferable implements Transferable, ClipboardOwner {

    /** Data Flavor of this class */
    public static DataFlavor orderListFlavor = new DataFlavor(open.dolphin.client.OrderList.class, "Order List");
    
    public static final DataFlavor[] flavors = {OrderListTransferable.orderListFlavor};
      
    private OrderList list;
    

    /** Creates new OrderListTransferable */
    public OrderListTransferable(OrderList list) {
        this.list = list;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
    	return flavors;
    }
     
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
    	return flavor.equals(orderListFlavor) ? true : false;
    }

    public synchronized Object getTransferData(DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {

        if (flavor.equals(orderListFlavor)) {
            return list;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

	public String toString() {
        return "Order List Transferable";
    }
  
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
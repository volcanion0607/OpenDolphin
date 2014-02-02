/*
 * DolphinFactory.java
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
package open.dolphin.project;

import java.awt.*;

import open.dolphin.client.*;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.ID;

/**
 * プロジェクトに依存するオブジェクトを生成するファクトリクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class DolphinFactory extends AbstractProjectFactory {
    
    protected String csgwPath;
    
    /** Creates new Project */
    public DolphinFactory() {
    }
    
    /**
     * 地域連携用の患者のMasterIdを返す。
     */
    public ID createMasterId(String pid, String facilityId) {
        return new ID(pid, "facility", facilityId);
    }
    
    /**
     * CSGW(Client Side Gate Way) のパスを返す。
     * 
     * @param  uploaderAddress MMLアップローダのIP Address
     * @param  share Samba 共有ディレクトリ
     * @param  facilityId 連携用の施設ID
     */
    public String createCSGWPath(String uploaderAddress, String share, String facilityId) {
        if (csgwPath == null) {
            if (ClientContext.isWin()) {
                StringBuilder sb = new StringBuilder();
                sb.append("\\\\");
                sb.append(uploaderAddress);
                sb.append("\\");
                sb.append(share);
                sb.append("\\");
                sb.append(facilityId);
                csgwPath = sb.toString();
            } else if (ClientContext.isMac()) {
                StringBuilder sb = new StringBuilder();
                sb.append("smb://");
                sb.append(uploaderAddress);
                sb.append("/");
                sb.append(share);
                sb.append("/");
                sb.append(facilityId);
                csgwPath = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("/");
                sb.append(uploaderAddress);
                sb.append("/");
                sb.append(share);
                sb.append("/");
                sb.append(facilityId);
                csgwPath = sb.toString();
            }
        }
        return csgwPath;
    }
    
    public Object createAboutDialog() {
        String title = ClientContext.getFrameTitle("アバウト");
        return new AboutDialog(null, title, "splash.jpg");
    }
    
    public Object createSaveDialog(Frame parent,SaveParams params) {
        SaveDialog sd = new SaveDialog(parent);
        params.setAllowPatientRef(false);    // 患者の参照
        params.setAllowClinicRef(false);     // 診療履歴のある医療機関
        sd.setValue(params);
        return sd;
    }
}
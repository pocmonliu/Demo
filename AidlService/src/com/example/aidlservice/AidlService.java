package com.example.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class AidlService extends Service{

    private AidlServiceImpl mAidlServiceImpl;
    
    private class AidlServiceImpl extends IAidlService.Stub{

        @Override
        public int plus(int a, int b) throws RemoteException {
            // TODO Auto-generated method stub
            return a + b;
        }

        @Override
        public String toUpperCase(String str) throws RemoteException {
            // TODO Auto-generated method stub
            if(str != null){
                return str.toUpperCase();
            }
            return null;
        }
        
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        
        if(mAidlServiceImpl == null){
            mAidlServiceImpl = new AidlServiceImpl();
        }
        
        return mAidlServiceImpl;
    }

}

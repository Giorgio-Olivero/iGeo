/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2011 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo.core;

import igeo.geo.*;

import java.util.ArrayList;

/**
   A server to take care of all IDynamicObject. It runs as separate thread.
   
   @author Satoru Sugihara
   @version 0.7.0.0
*/
public class IDynamicServer implements Runnable{
    
    public Thread thread;
    //public int speed = IConfig.dynamicsUpdateSpeed; // in millisecond
    //public int updateRate = (int)(IConfig.updateRate*1000); // in millisecond
    
    public IServer server;
    
    public boolean runningDynamics=false;
    public boolean startedOnce=false;
    
    public ArrayList<IDynamics> dynamics;
    
    public ArrayList<IDynamics> addingDynamics;
    public ArrayList<IDynamics> removingDynamics;
    
    public int duration = -1;
    public int time;
    
    public IDynamicServer(IServerI s){
	server = s.server();
	dynamics = new ArrayList<IDynamics>();

	addingDynamics = new ArrayList<IDynamics>();
	removingDynamics = new ArrayList<IDynamics>();
    }
    
    public synchronized void add(IObject e){
	for(IDynamics d:e.dynamics) add(d);
    }
    
    public synchronized void add(IDynamics e){
	// added object is once buffered in addingDynamics and actually added in the update cycle
	addingDynamics.add(e);
	
	/*
	if(!dynamics.contains(e)){
	    dynamics.add(e);
	    //if(IConfig.autoStart && !startedOnce) start(); // here is the point to start the thread. // not any more. it starts the first draw of IPanel
	}
	*/
    }

    /**************
       returns number of objects in dynamicServer but it
       includes objects to be added and excludes
       objects to be removed in next update cycle. 
    */
    /*
    public int num(){
	return dynamics.size()+addingDynamics.size()-removingDynamics.size();
	//return dynamics.size();
    }
    */
    /*************
       returns number of objects in dynamicServer ignoring 
       objects to be added and to be removed in next update cycle. 
    */
    //public int currentNum(){ return dynamics.size(); }
    /** get number of dynamic objects to be added in the next update cycle */
    public int addingNum(){ return addingDynamics.size(); }
    /** get number of dynamic objects to be removed in the next update cycle */
    public int removingNum(){ return removingDynamics.size(); }
    
    /** get number of current dynamic objects in the server */
    public int num(){ return dynamics.size(); }
    /** get i-th dynamic object in the server */
    public IDynamics get(int i){ return dynamics.get(i); }
    
    //public IDynamicServer updateRate(int rate){ updateRate = rate; return this; }
    //public int updateRate(){ return updateRate; }
    
    public synchronized void remove(int i){
	// removed object is once buffered in removingDynamics and actually removed in the update cycle
	removingDynamics.add(dynamics.get(i));
	//dynamics.remove(i);
    }
    public synchronized void remove(IDynamics d){
	// removed object is once buffered in removingDynamics and actually removed in the update cycle
	removingDynamics.add(d);
	//dynamics.remove(d);
    }
    
    public synchronized void clear(){
	addingDynamics.clear();
	removingDynamics.clear();
	dynamics.clear();
    }
    
    public IDynamicServer duration(int dur){ duration = dur; return this; }
    public int duration(){ return duration; }
    
    public IDynamicServer time(int tm){ time = tm; return this; }
    public int time(){ return time; }
    
    public void pause(){ runningDynamics=false; }
    public void resume(){ runningDynamics=true; }
    
    public void start(){
	if(!startedOnce && !runningDynamics && thread==null){
	    thread = new Thread(this);
	    runningDynamics=true;
	    startedOnce=true;
	    time=0;
	    thread.start();
	    IOut.debug(0,"dynamic server started");
	}
    }
    
    public void stop(){
	runningDynamics=false;
	thread=null;
	IOut.debug(0,"dynamic server stopped");
    }
    
    /** in case dynamicServer need to start again after stopped. */
    public void reset(){ startedOnce=false; }
    
    public void run(){
	Thread thisThread = Thread.currentThread();
	while(thread==thisThread){
	    
	    if(runningDynamics){
		if(duration>=0 && time>=duration){ stop(); break; }
		synchronized(this){
		    // adding objects
		    if(addingDynamics.size()>0){
			dynamics.addAll(addingDynamics);//any possible exception?
			addingDynamics.clear();
		    }
		    // removing objects
		    if(removingDynamics.size()>0){
			dynamics.removeAll(removingDynamics);//any possible exception?
			removingDynamics.clear();
		    }
		    
		    
		    for(int i=0; i<dynamics.size(); i++){
			dynamics.get(i).interact(dynamics); //
			//for(int j=i+1; j<dynamics.size(); j++) dynamics.get(i).interact(dynamics.get(j));
		    }
		    //for(IDynamics d:dynamics){ d.update(); }
		    for(int i=0; i<dynamics.size(); i++){
			dynamics.get(i).update();
		    }
		}
		time++;
		IOut.debug(20,"time="+time); //
	    }
	    
	    try{
		//Thread.sleep(updateRate);
		Thread.sleep((int)(IConfig.updateRate*1000));
	    }catch(InterruptedException e){}
	}
    }
}

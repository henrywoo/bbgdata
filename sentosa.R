#

getDuration=function(s){
  res=read.csv2(s,header=T,sep=",")
  e=as.vector(res$epochtime)
  tmp=as.double(e[1])
  v=c()
  for(i in e){
    d=(as.double(i)-tmp)
    tmp=as.double(i)
    v=c(v,d)
  }
  v2=v[v<3000]
  v2[2]=0
  
  par(mfrow=c(2,1))
  summary(v2)
  hist(v2,breaks=c(1:1000))
  plot(v2)
  title(s)
}

getDuration2=function(s){
  res=read.csv2(s,header=F,sep=",")
  colnames(res)=c('t','p','v','c')
  res[res$c!='U',]
  e=as.vector(res$t)
  #tmp=as.double(e[1])
  tmp <- strptime(e[1], "%d/%m/%Y %H:%M:%OS")
  v=c()
  for(i in e){
    t=strptime(i, "%d/%m/%Y %H:%M:%OS")
    d=difftime(t,tmp,units='secs')
    d=as.double(d)
    tmp=t
    v=c(v,d)
  }
  v2=v[v<3000]
  #v2[2]=0
  
  par(mfrow=c(2,1))
  
  summary(v2)
  head(v2)
  hist(v2)
  op <- par(oma=c(5,7,1,1))
  plot(v2)
  title(s)
}


#setwd('20130702')
setwd('l:/BBGData/ts/20130704')

fs=list.files()
#for (s in fs){
  print(s)
  #getDuration2(s)
  #if(substr(s,1,1)=="_"){
  #  print(s)
  #  getDuration(s)
    #print(s)
  #  break
  #}
  res=read.csv2('1766.csv',header=F,sep=",")
  colnames(res)=c('t','p','v','c')
  res=res[res$c!='U',]
  e=as.vector(res$t)
  #tmp=as.double(e[1])
  tmp <- strptime(e[1], "%m/%d/%Y %H:%M:%OS")
  v=c()
  for(i in e){
    t=strptime(i, "%m/%d/%Y %H:%M:%OS")
    d=difftime(t,tmp,units='secs')
    d=as.double(d)
    tmp=t
    v=c(v,d)
  }
  v2=v[v<3000]
  #v2[2]=0
  
  par(mfrow=c(2,1))
  
  #summary(v2)
  #head(v2)
  hist(v2)
  #op <- par(oma=c(5,7,1,1))
  plot(v2)
  title(s)
  break;
#}

setwd('../..')


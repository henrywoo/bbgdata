library(fPortfolio)

getDuration=function(s){
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

#function to output orderly max to min 
ppmatrix=function(m, r){
  mbackup=m
  if (isSymmetric.matrix(m)){
    m[lower.tri(m,diag = T)] = -100
  }
  ma=max(m)
  while(ma!=-100){
    cat(ma,sep="\t")
    cat("\t")
    w=which(m == max(m), arr.ind = TRUE)[1,]
    #print(w)
    col1=w[1]
    col2=w[2]
    cat(colnames(r[,col1]),sep="\t")
    cat("\t")
    cat(colnames(r[,col2]),sep="\n")
    m[col1,col2] = -100
    #print(col1)
    #print(col2)
    ma=max(m)
    #print(m)
    #Sys.sleep(2)
  }
  #m=mbackup
}



#setwd('20130702')
#setwd('l:/BBGData/ts/20130704')

fdir="ts/20130704_2/"
fs=list.files(path=fdir)
tot=NULL
for (s in fs){
  print(s)
  #getDuration2(s)
  #if(substr(s,1,1)=="_"){
  #  print(s)
  #  getDuration(s)
    #print(s)
  #  break
  fname=paste(fdir,s,sep='')
  res=read.csv2(fname,header=F,sep=",")
  colnames(res)=c('t','p','v','c')
  coln=dim(res)[2]-1
  #resXU=res[res$c!='U',][1:coln]
  resOC=res[res$c=='OC',][1:coln]
  #resU=res[res$c=='U',][1:coln]
  #resY=res[res$c=='Y',][1:coln]
  
  #charvec=as.vector(resU$t)
  #tmsU=timeSeries(resU[2:3],charvec,format="%m/%d/%Y %H:%M:%OS")
  #charvec=as.vector(resY$t)
  #tmsY=timeSeries(resY[2:3],charvec,format="%m/%d/%Y %H:%M:%OS")
  charvec=as.vector(resOC$t)
  s=gsub('.csv','',s)
  tmsOC=timeSeries(as.numeric(resOC[2:2]$p),charvec,units=s)
  
  tot=cbind(tot,tmsOC)
  if(dim(tot)[2]>5)
    break
}
#plot(tot[,1:10])
r=returns(tot)
#res=read.csv2('1766.csv',header=F,sep=",")
cr=cor(r)
diag(cr)=0
ppmatrix(cr,r)

res=read.csv2('ts/20130704/'+'1766.csv',header=F,sep=",")
colnames(res)=c('t','p','v','c')
coln=dim(res)[2]-1
resXU=res[res$c!='U',][1:coln]
resOC=res[res$c=='OC',][1:coln]
resU=res[res$c=='U',][1:coln]
resY=res[res$c=='Y',][1:coln]

charvec=as.vector(resU$t)
tmsU=timeSeries(resU[2:3],charvec,format="%m/%d/%Y %H:%M:%OS")
charvec=as.vector(resY$t)
tmsY=timeSeries(resY[2:3],charvec,format="%m/%d/%Y %H:%M:%OS")
charvec=as.vector(resOC$t)
tmsOC=timeSeries(resOC[2:2],charvec,units='1766.HK')

tot=cbind(tot,tmsOC)



tms=readSeries('ts/20130704/1766.csv',header=F,sep=",",format="%m/%d/%Y %H:%M:%OS")

colnames(tms)=c('p','v','c')
h=head(tms)
tmsXU=tms[tms$c!='U',]
tmsOC=tms[tms$c=='OC',]
tmsU=tms[tms$c=='U',]
tmsY=tms[tms$c=='Y',]
returns_tick=as.double(tms[,1:1])
returns_OC=as.double(tmsOC[,1:1])
returns_Y=as.double(tmsY[,1:1])
returns_U=as.double(tmsU[,1:1])
p=tmsOC[,1:1]

tms2=readSeries2('ts/20130704/1066.csv',header=F,sep=",",format="%m/%d/%Y %H:%M:%OS")
colnames(tms2)=c('p','v','c')

tmsU2=tms2[tms2$c=='U',]
returns_U2=as.double(tmsU2[,1:1])
tmsOC2=tms2[tms2$c=='OC',]
p2=tmsOC2[,1:1]
returns_OC2=as.double(tmsOC2[,1:1])

#plot(_returns)

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

#setwd('../..')


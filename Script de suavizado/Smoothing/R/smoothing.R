library(cobs)

getSmoothedVersion <- function(fileName) {

entrada <- read.csv(fileName, sep=" ",header = TRUE)
parametros <- colnames(entrada)[-ncol(entrada)]

spread <- unlist(c(entrada))
time <- 0:(length(spread)-1)

con2 <- rbind(c( 0,min(time),0))

Sb1 <- cobs(time,spread, nknots=8,pointwise= con2,lambda = 1,
            degree = 2)

summary(Sb1)

plot(Sb1, main=paste("Spread curve for",fileName))

mtr <-predict(Sb1,time)
colnames(mtr) <- c('timestamp','Spread')
mtr[1, 2] = 0

write.csv(mtr[,2, drop=FALSE],paste("output/",fileName,"Smooth.txt"), row.names = FALSE)
}

run <- {
pdf("output/outputPlot.pdf")
getSmoothedVersion(fileName = "popeSpread.txt")
getSmoothedVersion(fileName = "electionSpread.txt")
dev.off()
}

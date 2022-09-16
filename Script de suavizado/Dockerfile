FROM r-base
COPY ./Smoothing/R/ /usr/local/src/script
COPY ./Smoothing/electionSpread.txt /usr/local/src/script
COPY ./Smoothing/popeSpread.txt /usr/local/src/script
RUN mkdir -p /usr/local/src/script/output
WORKDIR /usr/local/src/script
RUN R -e "install.packages('cobs', type = 'source')"
CMD ["Rscript", "smoothing.R"]
FROM clojure

# To build Dockerfile:
#
#   docker build -t temp .
#
# To run this Dockerfile locally:
#
#   docker run -p 8080:7223 temp
#
# Then open localhost:8080 in a browser, outside of docker.
#
# TO run bash inside a one-off instance of this this container:
#
#   docker run --rm -it --entrypoint bash temp

# System depenencies
#
# 1. Pandoc is required for markdown conversion
# 2. tree is nice for debuggin'
RUN apt-get update && apt-get install -y tree pandoc && apt-get clean && rm -rf /var/lib/apt/lists/*

# Cache deps
RUN mkdir -p /olorm/
COPY deps.edn /olorm/deps.edn

WORKDIR /olorm/serve
RUN clj -e :deps-cached

# Copy files
COPY src/                /olorm/src/
COPY test/               /olorm/test/
COPY vanilla.css         /olorm/vanilla.css
COPY mikrobloggeriet.css /olorm/mikrobloggeriet.css
COPY o/                  /olorm/o
COPY j/                  /olorm/j

# Run test before deploy
WORKDIR /olorm
CMD clj -M:run-tests

# Init
CMD clj -X olorm.serve/start!

EXPOSE 7223

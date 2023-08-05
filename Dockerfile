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

# Cache deps (including test deps)
RUN mkdir -p /olorm/
COPY deps.edn /olorm/deps.edn

WORKDIR /olorm
RUN clj -A:test -e :deps-cached

# Copy files
COPY src/                /olorm/src/
COPY test/               /olorm/test/
COPY vanilla.css         /olorm/vanilla.css
COPY mikrobloggeriet.css /olorm/mikrobloggeriet.css
COPY o/                  /olorm/o
COPY j/                  /olorm/j

# Setup and run tests before deploy
ENV MIKROBLOGGIERIET_IN_DOCKER_BUILD=1
RUN mkdir -p "$HOME/.config/olorm/"
RUN echo "{:repo-path \"/olorm\"}" > "$HOME/.config/olorm/config.edn"
RUN git config --global user.name "HOPS Dockerfile"
RUN git config --global user.email "hops-dockerfile@ci.mikrobloggeriet.no"
RUN clj -M:run-tests

# Init
CMD clj -X olorm.serve/start!

EXPOSE 7223

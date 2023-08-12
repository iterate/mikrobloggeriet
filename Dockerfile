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
RUN mkdir -p /mikrobloggeriet/
COPY deps.edn /mikrobloggeriet/deps.edn

WORKDIR /mikrobloggeriet
RUN clj -A:test -e :deps-cached

# Copy files
COPY src/                /mikrobloggeriet/src/
COPY test/               /mikrobloggeriet/test/
COPY vanilla.css         /mikrobloggeriet/vanilla.css
COPY mikrobloggeriet.css /mikrobloggeriet/mikrobloggeriet.css
COPY o/                  /mikrobloggeriet/o
COPY j/                  /mikrobloggeriet/j

# Setup and run tests before deploy
ENV MIKROBLOGGIERIET_IN_DOCKER_BUILD=1
RUN mkdir -p "$HOME/.config/olorm/"
RUN echo "{:repo-path \"/mikrobloggeriet\"}" > "$HOME/.config/olorm/config.edn"
RUN git config --global user.name "HOPS Dockerfile"
RUN git config --global user.email "hops-dockerfile@ci.mikrobloggeriet.no"
RUN clj -M:run-tests

# Init
CMD clj -X mikrobloggeriet.serve/start!

EXPOSE 7223

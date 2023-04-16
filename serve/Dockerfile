FROM clojure

# We mirror olorm repo structure in the Docker filesystem:
#
#   | docker path   | content |
#   |---------------|---------|
#   | /olorm/serve/ | server  |
#   | /olorm/o/     | olorms  |
#   | /olorm/lib    | lib     |
#
# To build Dockerfile:
#
#   docker build -t temp -f "$(pwd)/Dockerfile" ..
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
# 2. tree is nice for debuggin
RUN apt-get update && apt-get install -y tree pandoc && apt-get clean && rm -rf /var/lib/apt/lists/*

# Cache deps
RUN mkdir -p /olorm/serve
COPY serve/deps.edn /olorm/serve/deps.edn
RUN mkdir -p /olorm/lib
COPY lib/deps.edn /olorm/lib/deps.edn

WORKDIR /olorm/serve
RUN clj -e :deps-cached

# Copy files
COPY serve/src/        /olorm/serve/src/
COPY serve/vanilla.css /olorm/serve/vanilla.css
COPY lib/              /olorm/lib/
COPY o/                /olorm/o

# Init
WORKDIR /olorm/serve
CMD clj -X olorm.serve/start!


EXPOSE 7223

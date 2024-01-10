(ns mikrobloggeriet.urlog4
  (:require
   [hiccup.page :as page]))

(defn logo []
  [:p {:class "logo"}
   "&nbsp;_&nbsp;&nbsp;&nbsp;_ ____&nbsp;&nbsp;_&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;___&nbsp;&nbsp;&nbsp;____ <br>
    | | | |&nbsp;&nbsp;_ \\| |&nbsp;&nbsp;&nbsp;/ _ \\ / ___|<br>
    | | | | |_) | |&nbsp;&nbsp;| | | | |&nbsp;&nbsp;_ <br>
    | |_| |&nbsp;&nbsp;_ <| |__| |_| | |_| |<br>
    &nbsp;\\___/|_| \\_\\_____\\___/ \\____|<br>"])

(defn door-0 [url]
  [:div {:class "component"}
   [:p "|____|____|____|____|____|____|____"]
   [:div {:class "wall"}
    [:p "__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>___|____<br>__|____|_"]
    [:a {:href url :class "door"}
     [:p {:class "closed"}
      "&nbsp;__|____|____|__<br>
      |\\ ___________ /|<br>
      | |&nbsp;&nbsp;_______&nbsp;&nbsp;| |<br>
      | | |___|___| | |<br>
      | | |___x___| | |<br>
      | | |___x___| | |<br>
      | | |___|___| | |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;[___] ()| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      |_|___________|_|<br>"]
     [:p {:class "open"}
      "
      &nbsp;__|____|____|__<br>
      |\\ ___________ /|<br>
      | |&nbsp;&nbsp;/|,| |&nbsp;&nbsp;&nbsp;| |<br>
      | | |,x,| |&nbsp;&nbsp;&nbsp;| |<br>
      | | |,x,' |&nbsp;&nbsp;&nbsp;| |<br>
      | | |,|&nbsp;&nbsp;&nbsp;,&nbsp;&nbsp;&nbsp;| |<br>
      | | |/&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;/]()&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;[/ &nbsp;|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,'&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;,'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
      |_|,'_________|_|<br>
      "]]
    [:p "__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>___|____<br>__|____|_"]]])

(defn urlogs [_req]
  (page/html5
   [:head (page/include-css "/urlog4.css")]
   [:body
    [:header
     (-> (logo))
     [:p {:class "intro"}
      "Tilfeldige dører til internettsteder som kan være<br> morsomme og/eller interessante å besøke en eller annen gang."]]
    [:div {:class "all-doors"}
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))
     (-> (door-0 ""))]]))

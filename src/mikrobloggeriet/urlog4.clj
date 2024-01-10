(ns mikrobloggeriet.urlog4
  (:require
   [hiccup.page :as page]
   [clojure.string :as str]))

(defn logo []
  [:p {:class "logo"}
   "&nbsp;_&nbsp;&nbsp;&nbsp;_ ____&nbsp;&nbsp;_&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;___&nbsp;&nbsp;&nbsp;____ <br>
    | | | |&nbsp;&nbsp;_ \\| |&nbsp;&nbsp;&nbsp;/ _ \\ / ___|<br>
    | | | | |_) | |&nbsp;&nbsp;| | | | |&nbsp;&nbsp;_ <br>
    | |_| |&nbsp;&nbsp;_ <| |__| |_| | |_| |<br>
    &nbsp;\\___/|_| \\_\\_____\\___/ \\____|<br>"])

(defn door-1 [url]
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
      |_|___________|_|"]
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
      |_|,'_________|_|"]]
    [:p "__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>___|____<br>__|____|_"]]])

(defn door-2 [url]
  [:div {:class "component"}
   [:p "|____|____|____|____|____|____|___"]
   [:div {:class "wall"}
    [:p "_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__"]
    [:a {:href url :class "door"}
     [:p {:class "closed"}
      "&nbsp;__|____|____|__<br>
      |\\ __________ /|<br>
      | |&nbsp;&nbsp;__&nbsp;&nbsp;__&nbsp;&nbsp;| |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |__||__| | |<br>
      | |&nbsp;&nbsp;__&nbsp;&nbsp;__()| |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |&nbsp;&nbsp;||&nbsp;&nbsp;| | |<br>
      | | |__||__| | |<br>
      |_|__________|_|"]
     [:p {:class "open"}
      "&nbsp;__|____|____|__<br>
    |\\ __________ /|<br>
    | |&nbsp;&nbsp;,|| ||&nbsp;&nbsp;| |<br>
    | | | || ||&nbsp;&nbsp;| |<br>
    | | | || ||&nbsp;&nbsp;| |<br>
    | | | || ||&nbsp;&nbsp;| |<br>
    | | | ||/ |&nbsp;&nbsp;| |<br>
    | | |/&nbsp;&nbsp;, () | |<br>
    | |&nbsp;&nbsp;, | ||&nbsp;&nbsp;| |<br>
    | | | || ||&nbsp;&nbsp;| |<br>
    | | | || ||&nbsp;&nbsp;| |<br>
    | | | ||/ |&nbsp;&nbsp;| |<br>
    | | |/&nbsp;&nbsp;&nbsp;,'&nbsp;&nbsp;| |<br>
    | |&nbsp;&nbsp;&nbsp;,'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| |<br>
    |_|,'________|_|"]]
    [:p "__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__<br>____|_____<br>__|____|__"]]])

(defn door-3 [url]
  [:div {:class "component"}
   [:p "|____|____|____|____|____|____|___"]
   [:div {:class "wall"}
    [:p "_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__<br>___|____<br>_|____|__"]
    [:a {:href url :class "door"}
     [:p {:class "closed"}
      "__|____|____|_____<br> 
      |_,-''______&lsquo;&lsquo;-.|_<br>
       _/,-'&nbsp;&nbsp;;&nbsp;&nbsp;! &lsquo;-.,\\_<br>
       //&nbsp;&nbsp;:&nbsp;&nbsp;!&nbsp;&nbsp;:&nbsp;&nbsp;.&nbsp;&nbsp;\\\\<br>
       ||&nbsp;&nbsp;:&nbsp;&nbsp;:&nbsp;&nbsp;.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;||<br>
       ||_ ;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;;&nbsp;&nbsp;||<br>
       ()| .&nbsp;&nbsp;:&nbsp;&nbsp;.&nbsp;&nbsp;!&nbsp;&nbsp;||<br>
       ||&rdquo;&nbsp;&nbsp;&nbsp;&nbsp;(##)&nbsp;&nbsp;_&nbsp;&nbsp;||<br>
       ||&nbsp;&nbsp;:&nbsp;&nbsp;;&lsquo;&lsquo;'&nbsp;(_) (|<br>
       ||&nbsp;&nbsp;:&nbsp;&nbsp;:&nbsp;&nbsp;.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;||<br>
       ()_ !&nbsp;&nbsp;,&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;&nbsp;||<br>
       ||| .&nbsp;&nbsp;.&nbsp;&nbsp;:&nbsp;&nbsp;:&nbsp;&nbsp;||<br>
       ||&rdquo; .&nbsp;&nbsp;:&nbsp;&nbsp;:&nbsp;&nbsp;.&nbsp;&nbsp;||<br>
       ||&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.&nbsp;&nbsp;:&nbsp;&nbsp;:&nbsp;&nbsp;||<br>
       ||____;----.____||"]
     [:p {:class "open"}
      "__|____|____|_____<br>
         |_,-‘’&lsquo;-.&nbsp;&nbsp;&nbsp;&lsquo;&lsquo;-.|_<br>
        _//&nbsp;&nbsp;! :.\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\_<br>
       // : : .&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\<br>
       || :&nbsp;&nbsp;&nbsp;&nbsp;; |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ||_; : .! |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ()|.&nbsp;&nbsp;{}&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ||&rdquo;&nbsp;&nbsp;;&lsquo;'()(&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       || : : . ;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       || : , : :|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ()_! . : .|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       |||. : .&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ||&rdquo;: . _.,’&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ||&nbsp;&nbsp;&nbsp;,´&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
       ||,’’____________|"]]
    [:p "_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_<br>___|____<br>_|____|_"]]])

(defn door-4 [url]
  [:div {:class "component"}
   [:p "|____|____|____|____|____|____|____"]
   [:div {:class "wall"}
    [:p "__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>____|____<br>__|____|_<br>___|____<br>__|____|_"]
    [:a {:href url :class "door"}
     [:p {:class "closed"}
      "&nbsp;__|____|____|__<br>
      |\\\\___________ //<br>
      | \\\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//|<br>
      | |\\\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// |<br>
      | | \\\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//| |<br>
      | |&nbsp;&nbsp;\\\\&nbsp;&nbsp;&nbsp;&nbsp;// | |<br>
      | |&nbsp;&nbsp;&nbsp;\\\\&nbsp;&nbsp;//&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;\\\\//&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\\\&nbsp;&nbsp;()| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;//\\\\&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;//&nbsp;&nbsp;\\\\&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;//&nbsp;&nbsp;&nbsp;&nbsp;\\\\ | |<br>
      | | //&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\\\| |<br>
      | |//&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\\\ |<br>
      |_//__________\\\\|"]
     [:p {:class "open"}
      "&nbsp;__|____|____|__<br>
      |\\\\___________ //<br>
      | \\\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;//|<br>
      | |\\\\&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;// |<br>
      | | \\\\&nbsp;&nbsp;&nbsp;&nbsp;| //| |<br>
      | |&nbsp;&nbsp;\\\\&nbsp;&nbsp;&nbsp;|// | |<br>
      | |&nbsp;&nbsp;&nbsp;\\\\&nbsp;&nbsp;//&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;\\\\//&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\\\|&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;&nbsp;//\\\\&nbsp;&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;&nbsp;//&nbsp;&nbsp;\\\\&nbsp;&nbsp;| |<br>
      | |&nbsp;&nbsp;//&nbsp;&nbsp;&nbsp;|\\\\ | |<br>
      | | //&nbsp;&nbsp;,'&nbsp;&nbsp;\\\\| |<br>
      | |//,'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\\\\ |<br>
      |_//__________\\\\|"]]
    [:p "_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__<br>___|_____<br>_|____|__"]]])

(def doors [door-1 door-2 door-3 door-4])

(defn rand-door [url]
  ((rand-nth doors) url))

(def urlfile-path "text/urlog3/urls.txt")
(defn parse-urlfile
  "Parse an urlfile into a vector of urls (strings).

  - Empty lines are ignored
  - Lines starting with # or whitespace then # are treated as comments
"
  [s]
  (->> (str/split-lines (str/trim s))
       (map str/trim)
       (remove str/blank?)
       (remove #(str/starts-with? % "#"))))

(comment
  (reverse (parse-urlfile (slurp urlfile-path))))

(defn urlogs [_req]
  (page/html5
   [:head
    (page/include-css "/urlog4.css")]
   [:body
    [:header
     (-> (logo))
     [:p {:class "intro"}
      "Tilfeldige dører til internettsteder som kan være morsomme og/eller interessante å besøke en eller annen gang."]]
    [:div {:class "all-doors"}
     (for [url (parse-urlfile (slurp urlfile-path))] (-> (rand-door url)))]]))
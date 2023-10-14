# mikrobloggeriet/ui

Idea: split ui components off from the rest of the app.

Motivation:

1. Reduce coupling between visuals and logic to make the app easier to grok and work with
2. Use a "card visualizer" to ease UI work.
   - We should get live reload without refreshing the web browser manually.
   - We should be able to see a UI component without navigating through the app UI.
   - We should be able to see different variants of a UI components next to each other for contrast
3. All app components should be possible to find in a tree structure
   - Right now, certain pages are hidden.
     For example https://mikrobloggeriet.no/olorm/draw/olr

## 2023-10-14

Direction / proposed approach:

1. Introduce Portfolio in ui/ folder
2. Make a version of https://mikrobloggeriet.no/olorm/draw/olr that works with portfolio
   - Why this page?
     Because it doesn't depend on global CSS, and is loosely coupled to other pages.
   

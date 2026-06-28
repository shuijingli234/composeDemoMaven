#!/bin/bash
set -e

REMOTE_URL="git@github.com:shuijingli234/composeDemoMaven.git"
BRANCH="gh-pages"
REPO_DIR="mavenPublishTestSDK/build/repo"

SKIP_PUBLISH=false
if [ "$1" = "--skip-publish" ]; then
    SKIP_PUBLISH=true
fi

if [ "$SKIP_PUBLISH" = false ]; then
    echo "[1/5] Publishing artifacts to local repo..."
    ./gradlew :mavenPublishTestSDK:publishAllPublicationsToGitHubPagesRepository
fi

echo "[$([ "$SKIP_PUBLISH" = true ] && echo "1" || echo "2")/5] Preparing Git repository..."
cd "$(dirname "$0")/$REPO_DIR"

if [ ! -d ".git" ]; then
    git init
fi

git remote remove origin 2>/dev/null || true
git remote add origin "$REMOTE_URL"

echo "[$([ "$SKIP_PUBLISH" = true ] && echo "2" || echo "3")/5] Switching to $BRANCH branch..."
git checkout -b "$BRANCH" 2>/dev/null || git checkout "$BRANCH"

echo "[$([ "$SKIP_PUBLISH" = true ] && echo "3" || echo "4")/5] Committing changes..."
git add -A
COMMIT_MSG="Update Maven artifacts $(date '+%Y-%m-%d %H:%M:%S')"
if git diff --cached --quiet; then
    echo "No changes to commit."
else
    git commit -m "$COMMIT_MSG"
fi

echo "[$([ "$SKIP_PUBLISH" = true ] && echo "4" || echo "5")/5] Pushing to $REMOTE_URL ($BRANCH)..."
git push -f origin "$BRANCH"

echo ""
echo "========================================"
echo "Published successfully!"
echo "Maven URL: https://shuijingli234.github.io/composeDemoMaven/"
echo "========================================"
